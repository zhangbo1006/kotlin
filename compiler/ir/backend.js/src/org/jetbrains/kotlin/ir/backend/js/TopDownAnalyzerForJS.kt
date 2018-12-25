/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.builtins.FunctionInterfacePackageFragment
import org.jetbrains.kotlin.builtins.KOTLIN_REFLECT_FQ_NAME
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.KotlinBuiltIns.BUILT_INS_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.builtins.functions.BuiltInFictitiousFunctionClassFactory
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.context.ModuleContext
import org.jetbrains.kotlin.context.MutableModuleContextImpl
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.deserialization.ClassDescriptorFactory
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.frontend.js.di.createTopDownAnalyzerForJs
import org.jetbrains.kotlin.incremental.components.ExpectActualTracker
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.js.analyzer.JsAnalysisResult
import org.jetbrains.kotlin.js.resolve.MODULE_KIND
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.TopDownAnalysisMode
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScopeImpl
import org.jetbrains.kotlin.serialization.js.ModuleKind
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.utils.Printer

class JsBuiltIns(storageManager: StorageManager) : KotlinBuiltIns(storageManager)

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

val kotlinReflect = KOTLIN_REFLECT_FQ_NAME
val kotlinBuiltins = BUILT_INS_PACKAGE_FQ_NAME

class FunctionInterfaceMemberScope(
    private val classDescriptorFactory: ClassDescriptorFactory,
    val packageName: FqName
) : MemberScopeImpl() {

    override fun getContributedDescriptors(
        kindFilter: DescriptorKindFilter,
        nameFilter: (Name) -> Boolean
    ) =
        classDescriptorFactory.getAllContributedClassesIfPossible(packageName)

    override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
        emptyList()

    override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
        emptyList()

    override fun getFunctionNames(): Set<Name> =
        emptySet()

    override fun getVariableNames(): Set<Name> =
        emptySet()

    override fun getClassifierNames(): Set<Name>? = null

    override fun printScopeStructure(p: Printer) {
        TODO("MemberScope.printScopeStructure is not implemented")
    }

    override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? {
        if (classDescriptorFactory.shouldCreateClass(kotlinBuiltins, name)) {
            return classDescriptorFactory.createClass(ClassId.topLevel(kotlinBuiltins.child(name)))
        }
        if (classDescriptorFactory.shouldCreateClass(kotlinReflect, name)) {
            return classDescriptorFactory.createClass(ClassId.topLevel(kotlinReflect.child(name)))
        }
        if (name == Name.identifier("KFunction")) {
            return null
        }
        return null
    }
}

class FunctionInterfacePackageFragmentImpl(
    classDescriptorFactory: ClassDescriptorFactory,
    private val containingModule: ModuleDescriptor
) : FunctionInterfacePackageFragment {

    private val memberScopeObj = FunctionInterfaceMemberScope(classDescriptorFactory, kotlinBuiltins)

    override val fqName: FqName
        get() = kotlinBuiltins

    private val shortName = fqName.shortName()

    override fun getName(): Name = shortName

    override fun getContainingDeclaration(): ModuleDescriptor = containingModule

    override fun getOriginal(): DeclarationDescriptorWithSource = this
    override fun getSource(): SourceElement = SourceElement.NO_SOURCE
    override val annotations: Annotations = Annotations.EMPTY

    override fun <R : Any?, D : Any?> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R {
        return visitor.visitPackageFragmentDescriptor(this, data)
    }

    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>) {
        visitor.visitPackageFragmentDescriptor(this, null)
    }

    override fun getMemberScope() = memberScopeObj
}

class FunctionInterfacePackageFragmentProvider(
    classFactory: BuiltInFictitiousFunctionClassFactory,
    module: ModuleDescriptor
) : PackageFragmentProvider {
    private val packageFragment = FunctionInterfacePackageFragmentImpl(classFactory, module)

    override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> {
        if (fqName != kotlinReflect && fqName != kotlinBuiltins)
            return emptyList()
        return listOf(packageFragment)
    }

    override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> =
        emptyList()
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

    val classFactory = BuiltInFictitiousFunctionClassFactory(moduleContext.storageManager, moduleContext.module)
    val packageFragment = FunctionInterfacePackageFragmentProvider(classFactory, moduleContext.module)

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