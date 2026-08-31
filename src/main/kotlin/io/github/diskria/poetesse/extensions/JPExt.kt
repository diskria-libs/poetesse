package io.github.diskria.poetesse.extensions

import io.github.diskria.poetesse.java.*
import io.github.diskria.poetesse.kotlin.KPCodeBlock

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

fun JPCodeBlockBuilder.beginControlFlow(codeBlock: KPCodeBlock): JPCodeBlockBuilder =
    beginControlFlow($$"$L", codeBlock)

fun JPCodeBlockBuilder.nextControlFlow(codeBlock: KPCodeBlock): JPCodeBlockBuilder =
    nextControlFlow($$"$L", codeBlock)
