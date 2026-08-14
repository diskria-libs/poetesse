package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.java.JPTypeVariableName
import io.github.diskria.poetesse.kotlin.KPModifier
import io.github.diskria.poetesse.kotlin.KPTypeVariableName

class XTypeVariableName internal constructor(
    override val settings: Poetesse.Settings,
    val name: String,
    val bounds: List<XTypeName>,
    val variance: XVariance?,
    val isReified: Boolean,
    override val isNullable: Boolean,
) : XTypedTypeName<KPTypeVariableName, JPTypeVariableName>() {

    override fun interopToKotlinInternal(): KPTypeVariableName =
        KPTypeVariableName(
            name,
            bounds.map { it.interopToKotlin() },
            variance?.toKPModifier()
        ).copy(reified = isReified)

    override fun interopToJavaInternal(): JPTypeVariableName {
        if (variance != null) error("Java type variables doesn't support variance")
        if (isReified) error("Java type variables doesn't support reified")
        return JPTypeVariableName.get(name, *bounds.map { it.box().interopToJava() }.toTypedArray())
    }
}

private fun XVariance.toKPModifier(): KPModifier? =
    when (this) {
        XVariance.IN -> KPModifier.IN
        XVariance.OUT -> KPModifier.OUT
    }

private fun KPModifier?.toKVariance(): XVariance? =
    when (this) {
        KPModifier.IN -> XVariance.IN
        KPModifier.OUT -> XVariance.OUT
        else -> null
    }

@PublishedApi
context(scope: PoetesseScope)
internal fun KPTypeVariableName.asXTypeVariableName(): XTypeVariableName =
    XTypeVariableName(scope.settings, name, bounds.map { it.toXType() }, variance.toKVariance(), isReified, isNullable)

@PublishedApi
context(scope: PoetesseScope)
internal fun JPTypeVariableName.asXTypeVariableName(nullable: Boolean = false): XTypeVariableName =
    XTypeVariableName(scope.settings, name(), bounds().map { it.toXType() }, null, false, nullable)

enum class XVariance {
    IN,
    OUT,
}
