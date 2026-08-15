package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.java.JPTypeVariableName
import io.github.diskria.poetesse.kotlin.KPModifier
import io.github.diskria.poetesse.kotlin.KPTypeVariableName

class XTypeVariableName private constructor(
    config: Poetesse.Config,
    val name: String,
    val bounds: List<XTypeName>,
    val variance: XVariance?,
    val isReified: Boolean,
    override val isNullable: Boolean,
) : XTypedTypeName<KPTypeVariableName, JPTypeVariableName>(config) {

    override fun interopToKotlinInternal(): KPTypeVariableName =
        KPTypeVariableName(name, bounds.map { it.interopToKotlin() }, variance?.modifier).copy(reified = isReified)

    override fun interopToJavaInternal(): JPTypeVariableName {
        if (variance != null) error("Java type variables doesn't support variance")
        if (isReified) error("Java type variables doesn't support reified")
        return JPTypeVariableName.get(name, *bounds.map { it.box().interopToJava() }.toTypedArray())
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, bounds: List<XTypeName>, variance: XVariance?, isReified: Boolean, isNullable: Boolean) =
            XTypeVariableName(poetesse.config, name, bounds, variance, isReified, isNullable)
    }
}

enum class XVariance(internal val modifier: KPModifier) {

    IN(KPModifier.IN),
    OUT(KPModifier.OUT);

    internal companion object {
        fun of(modifier: KPModifier?): XVariance? =
            XVariance.entries.find { it.modifier == modifier }
    }
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPTypeVariableName.asXTypeVariableName() = with(poetesse) {
    XTypeVariableName.of(name, bounds.map { xType(it) }, XVariance.of(variance), isReified, isNullable)
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPTypeVariableName.asXTypeVariableName(nullable: Boolean = false) = with(poetesse) {
    XTypeVariableName.of(name(), bounds().map { xType(it) }, variance = null, isReified = false, nullable)
}
