package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.extensions.capitalized
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.interop.XWildcardTypeName.Companion.of
import io.github.diskria.poetesse.java.JPTypeVariableName
import io.github.diskria.poetesse.kotlin.KPTypeVariableName

interface XTypeVariableFactory : PoetesseScope

fun XTypeVariableFactory.xTypeVariable(
    name: String,
    bounds: Iterable<XTypeName> = emptyList(),
    variance: XVariance? = null,
    reified: Boolean = false,
    nullable: Boolean = false,
) = XTypeVariableName.of(name, bounds.toList(), variance, reified, nullable)

fun XTypeVariableFactory.xTypeVariable(
    name: String,
    vararg bounds: XTypeName,
    variance: XVariance? = null,
    reified: Boolean = false,
    nullable: Boolean = false,
) = xTypeVariable(name, bounds.asIterable(), variance, reified, nullable)

fun XTypeVariableFactory.xTypeVariable(
    bounds: Iterable<XTypeName> = emptyList(),
    variance: XVariance? = null,
    reified: Boolean = false,
    nullable: Boolean = false,
) = EagerDelegate { xTypeVariable(it.capitalized(), bounds, variance, reified, nullable) }

fun XTypeVariableFactory.xTypeVariable(
    vararg bounds: XTypeName, variance: XVariance? = null, reified: Boolean = false, nullable: Boolean = false,
) = EagerDelegate { xTypeVariable(it.capitalized(), bounds.asIterable(), variance, reified, nullable) }

fun XTypeVariableFactory.xTypeVariable(kp: KPTypeVariableName, nullable: Boolean = kp.isNullable): XTypeVariableName =
    kp.setNullable(nullable).asXTypeVariableName()

fun XTypeVariableFactory.xTypeVariable(jp: JPTypeVariableName, nullable: Boolean = false): XTypeVariableName =
    jp.asXTypeVariableName(nullable)

fun XTypeVariableFactory.xWildcard(inT: XTypeName?, outT: XTypeName?): XWildcardTypeName =
    of(inType = inT, outType = outT)

fun XTypeVariableFactory.xStar(): XWildcardTypeName = xWildcard(null, null)
