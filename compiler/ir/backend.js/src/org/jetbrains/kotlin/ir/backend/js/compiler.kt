/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.backend.common.CompilerPhaseManager
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrPackageFragment
import org.jetbrains.kotlin.ir.util.SymbolTable
import org.jetbrains.kotlin.js.analyze.TopDownAnalyzerFacadeForJS
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.progress.ProgressIndicatorAndCompilationCanceledStatus
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi2ir.Psi2IrTranslator

data class Result(
    val moduleName: String,
    val generatedCode: String,
    val moduleFragment: IrModuleFragment,
    val moduleType: ModuleType,
    val dependencies: List<ModuleDependency>
)

fun Result.asModuleDependency(): ModuleDependency =
    ModuleDependency(moduleName, moduleFragment, moduleType, dependencies)

class ModuleDependency(
    val moduleName: String,
    val moduleFragment: IrModuleFragment,
    val moduleType: ModuleType,
    val dependencies: List<ModuleDependency>,
    val descriptor: ModuleDescriptorImpl = moduleFragment.descriptor as ModuleDescriptorImpl
)

fun ModuleDependency.getAllDeps(): List<ModuleDependency> = listOf(this) + dependencies.flatMap { it.getAllDeps() }
fun List<ModuleDependency>.getAllDeps(): List<ModuleDependency> = flatMap { it.getAllDeps() }.distinctBy { it.moduleName }


enum class ModuleType {
    TEST_RUNTIME,
    SECONDARY,
    MAIN
}

fun compile(
    project: Project,
    files: List<KtFile>,
    configuration: CompilerConfiguration,
    export: List<FqName> = emptyList(),
    dependencies: List<ModuleDependency> = emptyList(),
    builtInsModule: ModuleDependency? = null,
    moduleType: ModuleType
): Result {
    val analysisResult =
        TopDownAnalyzerFacadeForJS.analyzeFiles(
            files,
            project,
            configuration,
            dependencies.map { it.descriptor },
            emptyList(),
            thisIsBuiltInsModule = builtInsModule == null,
            customBuiltInsModule = builtInsModule?.descriptor
        )

    ProgressIndicatorAndCompilationCanceledStatus.checkCanceled()

    TopDownAnalyzerFacadeForJS.checkForErrors(files, analysisResult.bindingContext)

    val symbolTable = SymbolTable()
    dependencies.forEach { symbolTable.loadModule(it.moduleFragment)}

    val psi2IrTranslator = Psi2IrTranslator(configuration.languageVersionSettings)
    val psi2IrContext = psi2IrTranslator.createGeneratorContext(analysisResult.moduleDescriptor, analysisResult.bindingContext, symbolTable)

    val moduleFragment = psi2IrTranslator.generateModuleFragment(psi2IrContext, files)

    if (moduleType == ModuleType.MAIN) {
        for (dep in dependencies.getAllDeps()) {
            if (dep.moduleType == ModuleType.SECONDARY) {
                moduleFragment.files += dep.moduleFragment.files
            }
        }
    }

    val context = JsIrBackendContext(
        analysisResult.moduleDescriptor,
        psi2IrContext.irBuiltIns,
        psi2IrContext.symbolTable,
        moduleFragment,
        configuration,
        dependencies,
        moduleType
    )

    CompilerPhaseManager(context, context.phases, moduleFragment, JsPhaseRunner).run {
        jsPhases.fold(data) { m, p -> phase(p, context, m) }
    }

    val dependencies = dependencies.filter { it.moduleType == ModuleType.SECONDARY }

    val moduleName = configuration[CommonConfigurationKeys.MODULE_NAME]!!
    return Result(moduleName, context.jsProgram.toString(), context.moduleFragmentCopy, moduleType, dependencies)
}