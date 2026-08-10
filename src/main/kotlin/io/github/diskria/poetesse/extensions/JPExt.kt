package io.github.diskria.poetesse.extensions

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

fun JPTypeName.wrapToArray(): JPArrayTypeName =
    JPArrayTypeName.of(this)

fun JPClassName.parameterizedBy(vararg typeArguments: JPTypeName): JPParameterizedTypeName =
    JPParameterizedTypeName.get(this, *typeArguments)

fun JPClassName.parameterizedBy(typeArguments: Iterable<JPTypeName>): JPParameterizedTypeName =
    parameterizedBy(*typeArguments.toList().toTypedArray())
