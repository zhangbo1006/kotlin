/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.context.ModuleContext
import org.jetbrains.kotlin.context.MutableModuleContextImpl
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.frontend.js.di.createTopDownAnalyzerForJs
import org.jetbrains.kotlin.incremental.components.ExpectActualTracker
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.js.analyzer.JsAnalysisResult
import org.jetbrains.kotlin.js.resolve.MODULE_KIND
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.TopDownAnalysisMode
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import org.jetbrains.kotlin.serialization.js.ModuleKind
import org.jetbrains.kotlin.storage.StorageManager

class JsBuiltIns(storageManager: StorageManager) : KotlinBuiltIns(storageManager) {
    override fun getSuspendFunction(parameterCount: Int) =
        builtInsModule.findClassAcrossModuleDependencies(
            ClassId(
                DescriptorUtils.COROUTINES_PACKAGE_FQ_NAME_RELEASE,
                Name.identifier("SuspendFunction$parameterCount")
            )
        )!!

    override fun getFunction(parameterCount: Int): ClassDescriptor =
        builtInsModule.findClassAcrossModuleDependencies(
            ClassId(
                FqName("kotlin"),
                Name.identifier("Function$parameterCount")
            )
        )!!
}

fun analyzeFiles(
    files: Collection<KtFile>,
    project: Project,
    configuration: CompilerConfiguration,
    moduleDescriptors: List<ModuleDescriptorImpl>,
    friendModuleDescriptors: List<ModuleDescriptorImpl>,
    optionalBuiltInsModule: ModuleDescriptorImpl?
): JsAnalysisResult {
    val moduleName = configuration[CommonConfigurationKeys.MODULE_NAME]!!
    val projectContext = ProjectContext(project)

    val builtIns = JsBuiltIns(projectContext.storageManager)

    val module = ModuleDescriptorImpl(Name.special("<$moduleName>"), projectContext.storageManager, builtIns, null)

    val builtInsModule: ModuleDescriptorImpl = optionalBuiltInsModule ?: module
    builtIns.builtInsModule = builtInsModule

    val context = MutableModuleContextImpl(module, projectContext)

    context.module.setDependencies(
        listOf(context.module) +
                moduleDescriptors,
        friendModuleDescriptors.toSet()
    )

    val moduleKind = ModuleKind.PLAIN

    val trace = BindingTraceContext()
    trace.record(MODULE_KIND, context.module, moduleKind)
    return analyzeFilesWithGivenTrace(files, trace, context, configuration)
}

fun analyzeFilesWithGivenTrace(
    files: Collection<KtFile>,
    trace: BindingTrace,
    moduleContext: ModuleContext,
    configuration: CompilerConfiguration
): JsAnalysisResult {
    val lookupTracker = configuration.get(CommonConfigurationKeys.LOOKUP_TRACKER) ?: LookupTracker.DO_NOTHING
    val expectActualTracker = configuration.get(CommonConfigurationKeys.EXPECT_ACTUAL_TRACKER) ?: ExpectActualTracker.DoNothing
    val languageVersionSettings = configuration.languageVersionSettings
    val packageFragment = null
    val analyzerForJs = createTopDownAnalyzerForJs(
        moduleContext, trace,
        FileBasedDeclarationProviderFactory(moduleContext.storageManager, files),
        languageVersionSettings,
        lookupTracker,
        expectActualTracker,
        packageFragment
    )
    analyzerForJs.analyzeDeclarations(TopDownAnalysisMode.TopLevelDeclarations, files)
    return JsAnalysisResult.success(trace, moduleContext.module)
}