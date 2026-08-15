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
        context(scope: PoetesseXScope)
        fun of(name: String, bounds: List<XTypeName>, variance: XVariance?, isReified: Boolean, isNullable: Boolean) =
            XTypeVariableName(scope.config, name, bounds, variance, isReified, isNullable)
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
context(scope: PoetesseXScope)
internal fun KPTypeVariableName.asXTypeVariableName() =
    XTypeVariableName.of(name, bounds.map { scope.xType(it) }, XVariance.of(variance), isReified, isNullable)

@PublishedApi
context(scope: PoetesseXScope)
internal fun JPTypeVariableName.asXTypeVariableName(nullable: Boolean = false) =
    XTypeVariableName.of(name(), bounds().map { scope.xType(it) }, variance = null, isReified = false, nullable)
