package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.java.JPTypeVariableName
import io.github.diskria.poetesse.kotlin.KPCodeBlock
import io.github.diskria.poetesse.kotlin.KPModifier
import io.github.diskria.poetesse.kotlin.KPTypeVariableName
import javax.lang.model.SourceVersion

class XTypeVariableName private constructor(
    config: Poetesse.Config,
    val name: String,
    val bounds: List<XTypeName>,
    val variance: XVariance?,
    val isReified: Boolean,
    override val isNullable: Boolean,
) : XTypedTypeName<KPTypeVariableName, JPTypeVariableName>(config) {

    override fun interopToKotlinInternal(): KPTypeVariableName =
        KPTypeVariableName(
            name.escapeIfNecessary(),
            bounds.map { it.interopToKotlin() },
            variance?.modifier,
        ).copy(reified = isReified)

    override fun interopToJavaInternal(): JPTypeVariableName {
        require(variance == null) { "Java type variables doesn't support variance" }
        require(!isReified) { "Java type variables doesn't support reified" }
        val cleanName = name.unwrapBackticks()
        require(SourceVersion.isIdentifier(cleanName)) { "'$cleanName' is not a valid Java identifier name." }
        return JPTypeVariableName.get(cleanName, *bounds.map { it.box().interopToJava() }.toTypedArray())
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(
            name: String,
            bounds: List<XTypeName>,
            variance: XVariance?,
            isReified: Boolean,
            isNullable: Boolean = false
        ) = XTypeVariableName(poetesse.config, name, bounds, variance, isReified, isNullable)
    }
}

private fun String.unwrapBackticks(): String = removeSurrounding("`")
private fun String.escapeIfNecessary(): String = KPCodeBlock.of("%N", unwrapBackticks()).toString()

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
internal fun JPTypeVariableName.asXTypeVariableName(nullable: Boolean) = with(poetesse) {
    XTypeVariableName.of(name(), bounds().map { xType(it) }, variance = null, isReified = false, nullable)
}

fun XTypeVariableName.nullable(nullable: Boolean = true): XTypeVariableName =
    XTypeVariableName.of(name, bounds, variance, isReified, nullable)
