/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline.coroutines

import com.intellij.util.ArrayUtil
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.coroutines.*
import org.jetbrains.kotlin.codegen.inline.*
import org.jetbrains.kotlin.codegen.optimization.common.asSequence
import org.jetbrains.kotlin.config.isReleaseCoroutines
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.utils.addToStdlib.cast
import org.jetbrains.kotlin.utils.sure
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.tree.MethodInsnNode
import org.jetbrains.org.objectweb.asm.tree.MethodNode
import org.jetbrains.org.objectweb.asm.tree.TypeInsnNode

const val FOR_INLINE_SUFFIX = "\$\$forInline"

class CoroutineTransformer(
    private val inliningContext: InliningContext,
    private val classBuilder: ClassBuilder,
    private val sourceFile: String?,
    private val methods: List<MethodNode>,
    private val superClassName: String
) {
    private val state = inliningContext.state
    private val generateForInline = inliningContext.callSiteInfo.isInlineOrInsideInline

    fun shouldSkip(node: MethodNode): Boolean = methods.any { it.name == node.name + FOR_INLINE_SUFFIX && it.desc == node.desc }

    fun shouldGenerateStateMachine(node: MethodNode): Boolean {
        // Continuations are similar to lambdas from bird's view, but we should never generate state machine for them
        if (isContinuationNotLambda()) return false
        // The method captured crossinline lambda
        if (node.name.endsWith(FOR_INLINE_SUFFIX)) return true
        // Never generate state-machine for objects, which are going to be retransformed
        // See innerObjectRetransformation.kt
        // TODO: remove this check
        if (inliningContext.callSiteInfo.isInlineOrInsideInline) return false
        return when {
            isSuspendFunction(node) -> true
            // TODO: Find a reason, why I cannot remove this check yet
            isSuspendLambda(node) -> !isStateMachine(node)
            else -> false
        }
    }

    private fun isContinuationNotLambda(): Boolean = inliningContext.isContinuation &&
            if (state.languageVersionSettings.isReleaseCoroutines()) superClassName.endsWith("ContinuationImpl")
            else methods.any { it.name == "getLabel" }

    private fun crossinlineLambda(): PsiExpressionLambda? = inliningContext.expressionMap.values.find {
        it is PsiExpressionLambda && it.isCrossInline
    }?.cast()

    private fun isStateMachine(node: MethodNode): Boolean =
        node.instructions.asSequence().any { it.opcode == Opcodes.INVOKESTATIC && (it as MethodInsnNode).name == "getCOROUTINE_SUSPENDED" }

    private fun isSuspendLambda(node: MethodNode) = isResumeImpl(node)

    fun newMethod(node: MethodNode): DeferredMethodVisitor {
        val element = crossinlineLambda()?.functionWithBodyOrCallableReference.sure {
            "crossinline lambda should have element"
        }
        return when {
            isResumeImpl(node) -> {
                assert(!isStateMachine(node)) {
                    "Inlining/transforming state-machine"
                }
                newStateMachineForLambda(node, element)
            }
            isSuspendFunction(node) -> newStateMachineForNamedFunction(node, element)
            else -> error("no need to generate state maching for ${node.name}")
        }
    }

    private fun isResumeImpl(node: MethodNode): Boolean =
        state.languageVersionSettings.isResumeImplMethodName(node.name.removeSuffix(FOR_INLINE_SUFFIX)) &&
                inliningContext.isContinuation

    private fun isSuspendFunction(node: MethodNode): Boolean = findFakeContinuationConstructorClassName(node) != null

    private fun newStateMachineForLambda(node: MethodNode, element: KtElement): DeferredMethodVisitor {
        val name = node.name.removeSuffix(FOR_INLINE_SUFFIX)
        return DeferredMethodVisitor(
            MethodNode(
                node.access, name, node.desc, node.signature,
                ArrayUtil.toStringArray(node.exceptions)
            )
        ) {
            val stateMachineBuilder = CoroutineTransformerMethodVisitor(
                classBuilder.newMethod(
                    JvmDeclarationOrigin.NO_ORIGIN, node.access, name, node.desc, node.signature, null
                ), node.access, name, node.desc, null, null,
                obtainClassBuilderForCoroutineState = { classBuilder },
                element = element,
                diagnostics = state.diagnostics,
                languageVersionSettings = state.languageVersionSettings,
                shouldPreserveClassInitialization = state.constructorCallNormalizationMode.shouldPreserveClassInitialization,
                containingClassInternalName = classBuilder.thisName,
                isForNamedFunction = false,
                sourceFile = sourceFile ?: "",
                isCrossinlineLambda = inliningContext.isContinuation
            )

            if (generateForInline)
                MethodNodeCopyingMethodVisitor(
                    stateMachineBuilder, node.access, name, node.desc, node.signature, null,
                    codegen = null,
                    classBuilder = classBuilder,
                    keepAccess = true
                )
            else
                stateMachineBuilder
        }
    }

    private fun newStateMachineForNamedFunction(node: MethodNode, element: KtElement): DeferredMethodVisitor {
        val name = node.name.removeSuffix(FOR_INLINE_SUFFIX)
        val continuationClassName = findFakeContinuationConstructorClassName(node)
        assert(inliningContext is RegeneratedClassContext)
        return DeferredMethodVisitor(
            MethodNode(
                node.access, name, node.desc, node.signature,
                ArrayUtil.toStringArray(node.exceptions)
            )
        ) {
            val stateMachineBuilder = CoroutineTransformerMethodVisitor(
                classBuilder.newMethod(
                    JvmDeclarationOrigin.NO_ORIGIN, node.access, name, node.desc, node.signature,
                    ArrayUtil.toStringArray(node.exceptions)
                ), node.access, name, node.desc, null, null,
                obtainClassBuilderForCoroutineState = { (inliningContext as RegeneratedClassContext).continuationBuilders[continuationClassName]!! },
                element = element,
                diagnostics = state.diagnostics,
                languageVersionSettings = state.languageVersionSettings,
                shouldPreserveClassInitialization = state.constructorCallNormalizationMode.shouldPreserveClassInitialization,
                containingClassInternalName = classBuilder.thisName,
                isForNamedFunction = true,
                needDispatchReceiver = true,
                internalNameForDispatchReceiver = classBuilder.thisName,
                sourceFile = sourceFile ?: ""
            )

            if (generateForInline)
                MethodNodeCopyingMethodVisitor(
                    stateMachineBuilder, node.access, name, node.desc, node.signature, null,
                    codegen = null,
                    classBuilder = classBuilder,
                    keepAccess = true
                )
            else
                stateMachineBuilder
        }
    }

    fun replaceFakesWithReals(node: MethodNode) {
        findFakeContinuationConstructorClassName(node)?.let(::unregisterClassBuilder)?.let(ClassBuilder::done)
        replaceFakeContinuationsWithRealOnes(
            node, if (!inliningContext.isContinuation) getLastParameterIndex(node.desc, node.access) else 0
        )
    }

    fun registerClassBuilder(continuationClassName: String) {
        val context = inliningContext.parent?.parent as? RegeneratedClassContext ?: error("incorrect context")
        context.continuationBuilders[continuationClassName] = classBuilder
    }

    fun unregisterClassBuilder(continuationClassName: String) =
        (inliningContext as RegeneratedClassContext).continuationBuilders.remove(continuationClassName)

    companion object {
        fun findFakeContinuationConstructorClassName(node: MethodNode): String? {
            val marker = node.instructions.asSequence().firstOrNull(::isBeforeFakeContinuationConstructorCallMarker) ?: return null
            val new = marker.next
            assert(new?.opcode == Opcodes.NEW)
            return (new as TypeInsnNode).desc
        }
    }
}