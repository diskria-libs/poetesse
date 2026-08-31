package io.github.diskria.poetesse.extensions

import io.github.diskria.poetesse.interop.XCodeBlockMutationType
import io.github.diskria.poetesse.java.*

val JPTypeName.isVoid: Boolean
    get() = withoutAnnotations() == JPVoid

val JPTypeName.isBoxedVoid: Boolean
    get() = withoutAnnotations() == JPBoxedVoid

fun JPTypeName.setBoxed(boxed: Boolean): JPTypeName {
    val isBoxed = isBoxedVoid || isBoxedPrimitive
    if (isBoxed == boxed) return this
    return if (boxed) box() else unbox()
}

val JPClassName.binaryName: String get() = reflectionName()

val JPClassName.internalName: String get() = binaryName.replace('.', '/')

val JPClassName.qualifiedName: String get() = canonicalName()

fun JPTypeName.wrapToArray(): JPArrayTypeName =
    JPArrayTypeName.of(this)

fun JPClassName.parameterizedBy(vararg typeArguments: JPTypeName): JPParameterizedTypeName =
    JPParameterizedTypeName.get(this, *typeArguments)

fun JPClassName.parameterizedBy(typeArguments: Iterable<JPTypeName>): JPParameterizedTypeName =
    parameterizedBy(*typeArguments.toList().toTypedArray())

fun JPCodeBlockBuilder.beginControlFlow(codeBlock: JPCodeBlock): JPCodeBlockBuilder =
    beginControlFlow($$"$L", codeBlock)

fun JPCodeBlockBuilder.nextControlFlow(codeBlock: JPCodeBlock): JPCodeBlockBuilder =
    nextControlFlow($$"$L", codeBlock)

fun JPCodeBlockBuilder.endControlFlow(codeBlock: JPCodeBlock): JPCodeBlockBuilder =
    add($$"$<}$L;\n", codeBlock)

internal fun JPMethodBuilder.applyCodeBlockMutation(type: XCodeBlockMutationType, codeBlock: JPCodeBlock) {
    when (type) {
        XCodeBlockMutationType.ADD_STATEMENT -> addStatement(codeBlock)
        XCodeBlockMutationType.BEGIN_CONTROL_FLOW -> beginControlFlow(codeBlock)
        XCodeBlockMutationType.NEXT_CONTROL_FLOW -> nextControlFlow(codeBlock)
        XCodeBlockMutationType.END_CONTROL_FLOW -> {
            if (codeBlock.isEmpty) endControlFlow()
            else addCode($$"$<}$L;\n", codeBlock)
        }
    }
}

internal fun JPCodeBlockBuilder.applyCodeBlockMutation(type: XCodeBlockMutationType, codeBlock: JPCodeBlock) {
    when (type) {
        XCodeBlockMutationType.ADD_STATEMENT -> addStatement(codeBlock)
        XCodeBlockMutationType.BEGIN_CONTROL_FLOW -> beginControlFlow(codeBlock)
        XCodeBlockMutationType.NEXT_CONTROL_FLOW -> nextControlFlow(codeBlock)
        XCodeBlockMutationType.END_CONTROL_FLOW -> {
            if (codeBlock.isEmpty) endControlFlow()
            else endControlFlow(codeBlock)
        }
    }
}
