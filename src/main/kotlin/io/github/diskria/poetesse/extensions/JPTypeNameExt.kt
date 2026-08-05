package io.github.diskria.poetesse.extensions

import io.github.diskria.poetesse.java.JPArrayTypeName
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.java.JPParameterizedTypeName
import io.github.diskria.poetesse.java.JPTypeName

private val BOXED_VOID: JPTypeName = JPTypeName.VOID.box()

val JPTypeName.isVoid: Boolean
    get() = withoutAnnotations() == JPTypeName.VOID

val JPTypeName.isVoidOrPrimitive: Boolean
    get() = isVoid || isPrimitive

val JPTypeName.isBoxedVoid: Boolean
    get() = withoutAnnotations() == BOXED_VOID

val JPTypeName.isBoxedVoidOrPrimitive: Boolean
    get() = isBoxedVoid || isBoxedPrimitive

fun JPTypeName.setBoxed(boxed: Boolean): JPTypeName =
    if (isBoxedVoidOrPrimitive == boxed) this
    else {
        if (boxed) box()
        else unbox()
    }

fun JPTypeName.wrapIntoArrayTypeName(): JPArrayTypeName =
    JPArrayTypeName.of(this)

fun JPClassName.parameterizedBy(vararg typeArguments: JPTypeName): JPParameterizedTypeName =
    JPParameterizedTypeName.get(this, *typeArguments)

fun JPClassName.parameterizedBy(typeArguments: Iterable<JPTypeName>): JPParameterizedTypeName =
    parameterizedBy(*typeArguments.toList().toTypedArray())
