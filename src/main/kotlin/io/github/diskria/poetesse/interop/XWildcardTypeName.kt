package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.java.JPObject
import io.github.diskria.poetesse.java.JPWildcardTypeName
import io.github.diskria.poetesse.kotlin.KPAny
import io.github.diskria.poetesse.kotlin.KPStar
import io.github.diskria.poetesse.kotlin.KPWildcardTypeName

class XWildcardTypeName(
    val inType: XTypeName? = null,
    val outType: XTypeName? = null,
    override val nullable: Boolean = false,
) : XTypeName() {

    override fun interopToKotlin(): KPWildcardTypeName = when {
        inType != null -> KPWildcardTypeName.consumerOf(inType.interopToKotlin())
        outType != null -> {
            val kotlinBound = outType.interopToKotlin()
            if (kotlinBound == KPAny.copy(nullable = true)) {
                KPStar
            } else {
                KPWildcardTypeName.producerOf(kotlinBound)
            }
        }

        else -> KPStar
    }.setNullable(nullable)

    override fun interopToJava(): JPWildcardTypeName = when {
        inType != null -> JPWildcardTypeName.supertypeOf(inType.interopToJava())
        outType != null -> JPWildcardTypeName.subtypeOf(outType.interopToJava())
        else -> JPWildcardTypeName.subtypeOf(JPObject)
    }

    override fun setNullableInternal(nullable: Boolean): XWildcardTypeName =
        XWildcardTypeName(inType, outType, nullable)
}

fun KPWildcardTypeName.asXWildcardTypeName(): XWildcardTypeName {
    if (inTypes.size > 1 || outTypes.size > 1) {
        error("Unsupported wildcard with multiple bounds")
    }
    return XWildcardTypeName(
        inType = inTypes.firstOrNull()?.asXTypeName(),
        outType = outTypes.firstOrNull()?.asXTypeName(),
        nullable = isNullable
    )
}

fun JPWildcardTypeName.asXWildcardTypeName(): XWildcardTypeName {
    if (lowerBounds().size > 1 || upperBounds().size > 1) {
        error("Unsupported wildcard with multiple bounds")
    }
    return XWildcardTypeName(
        inType = lowerBounds().firstOrNull()?.asXTypeName(),
        outType = if (lowerBounds().isNotEmpty()) null else upperBounds().firstOrNull()?.asXTypeName()
    )
}
