package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.java.JPObject
import io.github.diskria.poetesse.java.JPWildcardTypeName
import io.github.diskria.poetesse.kotlin.KPAny
import io.github.diskria.poetesse.kotlin.KPStar
import io.github.diskria.poetesse.kotlin.KPWildcardTypeName

class XWildcardTypeName internal constructor(
    override val settings: Poetesse.Settings,
    val inType: XTypeName<*, *>?,
    val outType: XTypeName<*, *>?,
    override val isNullable: Boolean,
) : XTypeName<KPWildcardTypeName, JPWildcardTypeName>() {

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
}

@PublishedApi
context(scope: PoetesseScope)
internal fun KPWildcardTypeName.asXWildcardTypeName(): XWildcardTypeName =
    XWildcardTypeName(
        settings = scope.settings,
        inType = inTypes.firstOrNull()?.toXType(),
        outType = outTypes.firstOrNull()?.toXType(),
        isNullable = isNullable,
    )

@PublishedApi
context(scope: PoetesseScope)
internal fun JPWildcardTypeName.asXWildcardTypeName(nullable: Boolean = false): XWildcardTypeName =
    XWildcardTypeName(
        settings = scope.settings,
        inType = lowerBounds().firstOrNull()?.toXType(),
        outType = if (lowerBounds().isNotEmpty()) null else upperBounds().firstOrNull()?.toXType(),
        isNullable = nullable,
    )

fun XTypeName<*, *>.consumer(nullable: Boolean = false): XWildcardTypeName =
    XWildcardTypeName(settings, inType = this, outType = null, isNullable = nullable)

fun XTypeName<*, *>.producer(nullable: Boolean = false): XWildcardTypeName =
    XWildcardTypeName(settings, outType = this, inType = null, isNullable = nullable)
