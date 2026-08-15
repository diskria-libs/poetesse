package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.extensions.capitalized

interface XTypeVariableFactory : PoetesseScope

fun XTypeVariableFactory.xTypeVariable(
    name: String, bounds: Iterable<XTypeName> = emptyList(), variance: XVariance? = null, reified: Boolean = false,
    nullable: Boolean = false,
) = XTypeVariableName.of(name, bounds.toList(), variance, reified, nullable)

fun XTypeVariableFactory.xTypeVariable(
    name: String, vararg bounds: XTypeName, variance: XVariance? = null, reified: Boolean = false,
    nullable: Boolean = false,
) = xTypeVariable(name, bounds.asIterable(), variance, reified, nullable)

fun XTypeVariableFactory.xTypeVariable(
    bounds: Iterable<XTypeName> = emptyList(), variance: XVariance? = null, reified: Boolean = false,
    nullable: Boolean = false,
) = EagerDelegate { xTypeVariable(it.capitalized(), bounds, variance, reified, nullable) }

fun XTypeVariableFactory.xTypeVariable(
    vararg bounds: XTypeName, variance: XVariance? = null, reified: Boolean = false, nullable: Boolean = false,
) = EagerDelegate { xTypeVariable(it.capitalized(), bounds.asIterable(), variance, reified, nullable) }
