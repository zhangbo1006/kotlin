/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.js

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.util.ExceptionUtil
import com.intellij.util.SmartList
import org.jetbrains.kotlin.cli.common.*
import org.jetbrains.kotlin.cli.common.ExitCode.COMPILATION_ERROR
import org.jetbrains.kotlin.cli.common.ExitCode.OK
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.K2JsArgumentConstants
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoot
import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.*
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.cli.common.output.writeAll
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.plugins.PluginCliParser
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.incremental.components.ExpectActualTracker
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.incremental.js.IncrementalDataProvider
import org.jetbrains.kotlin.incremental.js.IncrementalResultsConsumer
import org.jetbrains.kotlin.js.analyze.TopDownAnalyzerFacadeForJS
import org.jetbrains.kotlin.js.analyzer.JsAnalysisResult
//import org.jetbrains.kotlin.js.config.EcmaVersion
//import org.jetbrains.kotlin.js.config.JSConfigurationKeys
//import org.jetbrains.kotlin.js.config.JsConfig
//import org.jetbrains.kotlin.js.config.SourceMapSourceEmbedding
//import org.jetbrains.kotlin.js.facade.K2JSTranslator
//import org.jetbrains.kotlin.js.facade.MainCallParameters
//import org.jetbrains.kotlin.js.facade.TranslationResult
//import org.jetbrains.kotlin.js.facade.TranslationUnit
//import org.jetbrains.kotlin.js.facade.exceptions.TranslationException
//import org.jetbrains.kotlin.js.sourceMap.SourceFilePathResolver
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.progress.ProgressIndicatorAndCompilationCanceledStatus
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.serialization.js.ModuleKind
import org.jetbrains.kotlin.utils.*
import java.io.File
import java.io.IOException
import java.util.*
//import org.jetbrains.kotlin.ir.backend.js.*
import org.jetbrains.kotlin.js.config.EcmaVersion
import org.jetbrains.kotlin.js.config.JSConfigurationKeys
import org.jetbrains.kotlin.js.config.JsConfig
import org.jetbrains.kotlin.js.config.SourceMapSourceEmbedding
import org.jetbrains.kotlin.js.facade.MainCallParameters
import org.jetbrains.kotlin.js.facade.TranslationResult
import org.jetbrains.kotlin.js.facade.TranslationUnit
import org.jetbrains.kotlin.js.sourceMap.SourceFilePathResolver

class K2JsIrCompiler : CLICompiler<K2JSCompilerArguments>() {

    override val performanceManager: CommonCompilerPerformanceManager =
        object : CommonCompilerPerformanceManager("Kotlin to JS (IR) Compiler") {}

    override fun createArguments(): K2JSCompilerArguments {
        return K2JSCompilerArguments()
    }

    override fun doExecute(
        arguments: K2JSCompilerArguments,
        configuration: CompilerConfiguration,
        rootDisposable: Disposable,
        paths: KotlinPaths?
    ): ExitCode {
        val messageCollector = configuration.getNotNull(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY)

        if (arguments.freeArgs.isEmpty() && !IncrementalCompilation.isEnabledForJs()) {
            if (arguments.version) {
                return OK
            }
            messageCollector.report(ERROR, "Specify at least one source file or directory", null)
            return COMPILATION_ERROR
        }

        val pluginLoadResult = PluginCliParser.loadPluginsSafe(
            arguments.pluginClasspaths,
            arguments.pluginOptions,
            configuration
        )
        if (pluginLoadResult != ExitCode.OK) return pluginLoadResult

        configuration.put(JSConfigurationKeys.LIBRARIES, configureLibraries(arguments, paths, messageCollector))

        val commonSourcesArray = arguments.commonSources
        val commonSources = commonSourcesArray?.let { setOf(it) } ?: emptySet()
        for (arg in arguments.freeArgs) {
            configuration.addKotlinSourceRoot(arg, commonSources.contains(arg))
        }

        val environmentForJS =
            KotlinCoreEnvironment.createForProduction(rootDisposable, configuration, EnvironmentConfigFiles.JS_CONFIG_FILES)

        val project = environmentForJS.project
        val sourcesFiles = environmentForJS.getSourceFiles()

        environmentForJS.configuration.put(CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE, arguments.allowKotlinPackage)

        if (!checkKotlinPackageUsage(environmentForJS, sourcesFiles)) return ExitCode.COMPILATION_ERROR

        val outputFilePath = arguments.outputFile
        if (outputFilePath == null) {
            messageCollector.report(ERROR, "Specify output file via -output", null)
            return ExitCode.COMPILATION_ERROR
        }

        if (messageCollector.hasErrors()) {
            return ExitCode.COMPILATION_ERROR
        }

        if (sourcesFiles.isEmpty() && !IncrementalCompilation.isEnabledForJs()) {
            messageCollector.report(ERROR, "No source files", null)
            return COMPILATION_ERROR
        }

        if (arguments.verbose) {
            reportCompiledSourcesList(messageCollector, sourcesFiles)
        }

        val outputFile = File(outputFilePath)

        configuration.put(CommonConfigurationKeys.MODULE_NAME, FileUtil.getNameWithoutExtension(outputFile))

        val config = JsConfig(project, configuration)
        val reporter = object : JsConfig.Reporter() {
            override fun error(message: String) {
                messageCollector.report(ERROR, message, null)
            }

            override fun warning(message: String) {
                messageCollector.report(STRONG_WARNING, message, null)
            }
        }
        if (config.checkLibFilesAndReportErrors(reporter)) {
            return COMPILATION_ERROR
        }

        val analyzerWithCompilerReport = AnalyzerWithCompilerReport(
            messageCollector, configuration.languageVersionSettings
        )
        analyzerWithCompilerReport.analyzeAndReport(sourcesFiles) { TopDownAnalyzerFacadeForJS.analyzeFiles(sourcesFiles, config) }
        if (analyzerWithCompilerReport.hasErrors()) {
            return COMPILATION_ERROR
        }

        ProgressIndicatorAndCompilationCanceledStatus.checkCanceled()

        val analysisResult = analyzerWithCompilerReport.analysisResult
        assert(analysisResult is JsAnalysisResult) { "analysisResult should be instance of JsAnalysisResult, but $analysisResult" }
        val jsAnalysisResult = analysisResult as JsAnalysisResult

        val outputPrefix = arguments.outputPrefix
        var outputPrefixFile: File? = null
        if (outputPrefix != null) {
            outputPrefixFile = File(outputPrefix)
            if (!outputPrefixFile.exists()) {
                messageCollector.report(ERROR, "Output prefix file '$outputPrefix' not found", null)
                return ExitCode.COMPILATION_ERROR
            }
        }

        var outputPostfixFile: File? = null
        if (arguments.outputPostfix != null) {
            outputPostfixFile = File(arguments.outputPostfix!!)
            if (!outputPostfixFile.exists()) {
                messageCollector.report(ERROR, "Output postfix file '${arguments.outputPostfix}' not found", null)
                return ExitCode.COMPILATION_ERROR
            }
        }

        var outputDir: File? = outputFile.parentFile
        if (outputDir == null) {
            outputDir = outputFile.absoluteFile.parentFile
        }
        try {
            config.configuration.put(JSConfigurationKeys.OUTPUT_DIR, outputDir!!.canonicalFile)
        } catch (e: IOException) {
            messageCollector.report(ERROR, "Could not resolve output directory", null)
            return ExitCode.COMPILATION_ERROR
        }

        val mainCallParameters = createMainCallParameters(arguments.main)

        //val x = ::compile

        val translationResult: TranslationResult = try {
            TODO("Implement")
            // translate(reporter, sourcesFiles, jsAnalysisResult, mainCallParameters, config)
        } catch (e: Exception) {
            throw rethrow(e)
        }

        ProgressIndicatorAndCompilationCanceledStatus.checkCanceled()

        AnalyzerWithCompilerReport.reportDiagnostics(translationResult.diagnostics, messageCollector)

        if (translationResult !is TranslationResult.Success) return ExitCode.COMPILATION_ERROR

        val outputFiles = translationResult.getOutputFiles(outputFile, outputPrefixFile, outputPostfixFile)

        if (outputFile.isDirectory) {
            messageCollector.report(ERROR, "Cannot open output file '" + outputFile.path + "': is a directory", null)
            return ExitCode.COMPILATION_ERROR
        }

        ProgressIndicatorAndCompilationCanceledStatus.checkCanceled()

        outputFiles.writeAll(
            outputDir, messageCollector,
            configuration.getBoolean(CommonConfigurationKeys.REPORT_OUTPUT_FILES)
        )

        return OK
    }

    override fun setupPlatformSpecificArgumentsAndServices(
        configuration: CompilerConfiguration, arguments: K2JSCompilerArguments,
        services: Services
    ) {
        val messageCollector = configuration.getNotNull(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY)

        if (arguments.target != null) {
            assert("v5" == arguments.target) { "Unsupported ECMA version: " + arguments.target!! }
        }
        configuration.put(JSConfigurationKeys.TARGET, EcmaVersion.defaultVersion())

        // TODO: Support source maps
        if (arguments.sourceMap) {
            messageCollector.report(WARNING, "source-map argument is not supported yet", null)
        } else {
            if (arguments.sourceMapPrefix != null) {
                messageCollector.report(WARNING, "source-map-prefix argument has no effect without source map", null)
            }
            if (arguments.sourceMapBaseDirs != null) {
                messageCollector.report(WARNING, "source-map-source-root argument has no effect without source map", null)
            }
        }

        if (arguments.metaInfo) {
            configuration.put(JSConfigurationKeys.META_INFO, true)
        }

        configuration.put(JSConfigurationKeys.TYPED_ARRAYS_ENABLED, arguments.typedArrays)

        configuration.put(JSConfigurationKeys.FRIEND_PATHS_DISABLED, arguments.friendModulesDisabled)

        val friendModules = arguments.friendModules
        if (!arguments.friendModulesDisabled && friendModules != null) {
            val friendPaths = friendModules
                .split(File.pathSeparator.toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
                .filterNot { it.isEmpty() }

            configuration.put(JSConfigurationKeys.FRIEND_PATHS, friendPaths)
        }

        val moduleKindName = arguments.moduleKind
        var moduleKind: ModuleKind? = if (moduleKindName != null) moduleKindMap[moduleKindName] else ModuleKind.PLAIN
        if (moduleKind == null) {
            messageCollector.report(
                ERROR, "Unknown module kind: $moduleKindName. Valid values are: plain, amd, commonjs, umd", null
            )
            moduleKind = ModuleKind.PLAIN
        }
        configuration.put(JSConfigurationKeys.MODULE_KIND, moduleKind)

        val incrementalDataProvider = services[IncrementalDataProvider::class.java]
        if (incrementalDataProvider != null) {
            configuration.put(JSConfigurationKeys.INCREMENTAL_DATA_PROVIDER, incrementalDataProvider)
        }

        val incrementalResultsConsumer = services[IncrementalResultsConsumer::class.java]
        if (incrementalResultsConsumer != null) {
            configuration.put(JSConfigurationKeys.INCREMENTAL_RESULTS_CONSUMER, incrementalResultsConsumer)
        }

        val lookupTracker = services[LookupTracker::class.java]
        if (lookupTracker != null) {
            configuration.put(CommonConfigurationKeys.LOOKUP_TRACKER, lookupTracker)
        }

        val expectActualTracker = services[ExpectActualTracker::class.java]
        if (expectActualTracker != null) {
            configuration.put(CommonConfigurationKeys.EXPECT_ACTUAL_TRACKER, expectActualTracker)
        }

        val sourceMapEmbedContentString = arguments.sourceMapEmbedSources
        var sourceMapContentEmbedding: SourceMapSourceEmbedding? = if (sourceMapEmbedContentString != null)
            sourceMapContentEmbeddingMap[sourceMapEmbedContentString]
        else
            SourceMapSourceEmbedding.INLINING
        if (sourceMapContentEmbedding == null) {
            val message = "Unknown source map source embedding mode: " + sourceMapEmbedContentString + ". Valid values are: " +
                    StringUtil.join(sourceMapContentEmbeddingMap.keys, ", ")
            messageCollector.report(ERROR, message, null)
            sourceMapContentEmbedding = SourceMapSourceEmbedding.INLINING
        }
        configuration.put(JSConfigurationKeys.SOURCE_MAP_EMBED_SOURCES, sourceMapContentEmbedding)

        if (!arguments.sourceMap && sourceMapEmbedContentString != null) {
            messageCollector.report(WARNING, "source-map-embed-sources argument has no effect without source map", null)
        }
    }

    override fun executableScriptFileName(): String {
        return "kotlinc-js"
    }

    override fun createMetadataVersion(versionArray: IntArray): BinaryVersion {
        return JsMetadataVersion(*versionArray)
    }

    companion object {
        private val moduleKindMap = HashMap<String, ModuleKind>()
        private val sourceMapContentEmbeddingMap = LinkedHashMap<String, SourceMapSourceEmbedding>()

        init {
            moduleKindMap[K2JsArgumentConstants.MODULE_PLAIN] = ModuleKind.PLAIN
            moduleKindMap[K2JsArgumentConstants.MODULE_COMMONJS] = ModuleKind.COMMON_JS
            moduleKindMap[K2JsArgumentConstants.MODULE_AMD] = ModuleKind.AMD
            moduleKindMap[K2JsArgumentConstants.MODULE_UMD] = ModuleKind.UMD

            sourceMapContentEmbeddingMap[K2JsArgumentConstants.SOURCE_MAP_SOURCE_CONTENT_ALWAYS] = SourceMapSourceEmbedding.ALWAYS
            sourceMapContentEmbeddingMap[K2JsArgumentConstants.SOURCE_MAP_SOURCE_CONTENT_NEVER] = SourceMapSourceEmbedding.NEVER
            sourceMapContentEmbeddingMap[K2JsArgumentConstants.SOURCE_MAP_SOURCE_CONTENT_INLINING] = SourceMapSourceEmbedding.INLINING
        }

        @JvmStatic
        fun main(args: Array<String>) {
            CLITool.doMain(K2JsIrCompiler(), args)
        }

//        private fun translate(
//            reporter: JsConfig.Reporter,
//            allKotlinFiles: List<KtFile>,
//            jsAnalysisResult: JsAnalysisResult,
//            mainCallParameters: MainCallParameters,
//            config: JsConfig
//        ): TranslationResult {
//            allKotlinFiles.sortedBy { ktFile -> VfsUtilCore.virtualToIoFile(ktFile.virtualFile) }
//            return translator.translate(reporter, allKotlinFiles, mainCallParameters, jsAnalysisResult)
//        }

        private fun reportCompiledSourcesList(messageCollector: MessageCollector, sourceFiles: List<KtFile>) {
            val fileNames = sourceFiles.map { file ->
                val virtualFile = file.virtualFile
                if (virtualFile != null) {
                    MessageUtil.virtualFileToPath(virtualFile)
                } else {
                    file.name + " (no virtual file)"
                }
            }
            messageCollector.report(LOGGING, "Compiling source files: " + join(fileNames, ", "), null)
        }

        private fun configureLibraries(
            arguments: K2JSCompilerArguments,
            paths: KotlinPaths?,
            messageCollector: MessageCollector
        ): List<String> {
            val libraries = SmartList<String>()
            if (!arguments.noStdlib) {
                val stdlibJar = getLibraryFromHome(
                    paths, KotlinPaths::jsStdLibJarPath, PathUtil.JS_LIB_JAR_NAME, messageCollector, "'-no-stdlib'"
                )
                if (stdlibJar != null) {
                    libraries.add(stdlibJar.absolutePath)
                }
            }

            val argumentsLibraries = arguments.libraries
            if (argumentsLibraries != null) {
                libraries.addAll(
                    argumentsLibraries
                        .split(File.pathSeparator.toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                        .filterNot { it.isEmpty() }
                )
            }
            return libraries
        }

        private fun createMainCallParameters(main: String?): MainCallParameters {
            return if (K2JsArgumentConstants.NO_CALL == main) {
                MainCallParameters.noCall()
            } else {
                MainCallParameters.mainWithoutArguments()
            }
        }
    }
}
