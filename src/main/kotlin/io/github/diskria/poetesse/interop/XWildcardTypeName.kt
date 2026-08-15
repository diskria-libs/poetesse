package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.java.JPObject
import io.github.diskria.poetesse.java.JPWildcardTypeName
import io.github.diskria.poetesse.kotlin.KPAny
import io.github.diskria.poetesse.kotlin.KPStar
import io.github.diskria.poetesse.kotlin.KPWildcardTypeName

class XWildcardTypeName private constructor(
    config: Poetesse.Config,
    val inType: XTypeName?,
    val outType: XTypeName?,
    override val isNullable: Boolean,
) : XTypedTypeName<KPWildcardTypeName, JPWildcardTypeName>(config) {

    override fun interopToKotlinInternal(): KPWildcardTypeName = when {
        inType != null -> KPWildcardTypeName.consumerOf(inType.interopToKotlin())
        outType != null -> {
            val kpBound = outType.interopToKotlin()
            if (kpBound.withoutAnnotations() == KPAny.setNullable(true)) KPStar
            else KPWildcardTypeName.producerOf(kpBound)
        }

        else -> KPStar
    }

    override fun interopToJavaInternal(): JPWildcardTypeName = when {
        inType != null -> JPWildcardTypeName.supertypeOf(inType.box().interopToJava())
        outType != null -> JPWildcardTypeName.subtypeOf(outType.box().interopToJava())
        else -> JPWildcardTypeName.subtypeOf(JPObject)
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(inType: XTypeName?, outType: XTypeName?, isNullable: Boolean) =
            XWildcardTypeName(poetesse.config, inType, outType, isNullable)
    }
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPWildcardTypeName.asXWildcardTypeName() = with(poetesse) {
    XWildcardTypeName.of(
        inType = inTypes.firstOrNull()?.let { xType(it) },
        outType = outTypes.firstOrNull()?.let { xType(it) },
        isNullable = isNullable,
    )
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPWildcardTypeName.asXWildcardTypeName(nullable: Boolean = false) = with(poetesse) {
    XWildcardTypeName.of(
        inType = lowerBounds().firstOrNull()?.let { xType(it) },
        outType = if (lowerBounds().isNotEmpty()) null else upperBounds().firstOrNull()?.let { xType(it) },
        isNullable = nullable,
    )
}

fun XTypeName.consumer(nullable: Boolean = false) =
    XWildcardTypeName.of(inType = this, outType = null, isNullable = nullable)

fun XTypeName.producer(nullable: Boolean = false) =
    XWildcardTypeName.of(outType = this, inType = null, isNullable = nullable)
