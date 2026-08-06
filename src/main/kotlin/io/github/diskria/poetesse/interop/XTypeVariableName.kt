package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.java.JPTypeVariableName
import io.github.diskria.poetesse.kotlin.KPModifier
import io.github.diskria.poetesse.kotlin.KPTypeVariableName
import kotlin.reflect.KVariance

class XTypeVariableName internal constructor(
    val name: String,
    val bounds: List<XTypeName>,
    val variance: KVariance = KVariance.INVARIANT,
    val isReified: Boolean = false,
    override val nullable: Boolean = false,
) : XTypeName() {

    override fun interopToKotlin(): KPTypeVariableName =
        KPTypeVariableName(name, bounds.map { it.interopToKotlin() }, variance.toKPModifier())
            .copy(reified = isReified, nullable = nullable)

    override fun interopToJava(): JPTypeVariableName {
        if (variance != KVariance.INVARIANT) error("Java type variables doesn't support variance")
        if (isReified) error("Java type variables doesn't support reified")
        return JPTypeVariableName.get(name, *bounds.map { it.interopToJava() }.toTypedArray())
    }

    override fun setNullableInternal(nullable: Boolean): XTypeName =
        XTypeVariableName(name, bounds, variance, isReified, nullable)
}

fun KPTypeVariableName.asXTypeVariableName(): XTypeVariableName =
    XTypeVariableName(name, bounds.map { it.asXTypeName() }, variance.toKVariance())

fun JPTypeVariableName.asXTypeVariableName(): XTypeVariableName =
    XTypeVariableName(name(), bounds().map { it.asXTypeName() })

private fun KVariance.toKPModifier(): KPModifier? =
    when (this) {
        KVariance.INVARIANT -> null
        KVariance.IN -> KPModifier.IN
        KVariance.OUT -> KPModifier.OUT
    }

private fun KPModifier?.toKVariance(): KVariance =
    when (this) {
        KPModifier.IN -> KVariance.IN
        KPModifier.OUT -> KVariance.OUT
        else -> KVariance.INVARIANT
    }
