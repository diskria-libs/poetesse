package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.interop.XArrayTypeName.Companion.kotlinPrimitiveArrays
import io.github.diskria.poetesse.java.JPArrayTypeName
import io.github.diskria.poetesse.kotlin.KPArray
import io.github.diskria.poetesse.kotlin.KPClassName
import io.github.diskria.poetesse.kotlin.KPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPTypeName

class XArrayTypeName internal constructor(
    override val settings: Poetesse.Settings,
    val componentType: XTypeName<*, *>,
    override val isNullable: Boolean,
) : XTypeName<KPTypeName, JPArrayTypeName>() {

    override fun interopToKotlinInternal(): KPTypeName =
        if (componentType is XPrimitiveTypeName && !componentType.isBoxed) {
            componentType.kind.kotlinArray
        } else {
            KPArray.parameterizedBy(componentType.interopToKotlin())
        }

    override fun interopToJavaInternal(): JPArrayTypeName =
        JPArrayTypeName.of(componentType.interopToJava())

    internal companion object {
        val kotlinPrimitiveArrays = XPrimitiveTypeName.Kind.entries.associate { it.kotlinArray to it.kotlin }
    }
}

@PublishedApi
context(scope: PoetesseScope)
internal fun KPTypeName.asXArrayTypeNameOrNull(): XArrayTypeName? {
    val componentType = when (this) {
        is KPClassName -> kotlinPrimitiveArrays[this]?.asX<XPrimitiveTypeName>()
        is KPParameterizedTypeName if (rawType.setNullable(false).withoutAnnotations() == KPArray) -> {
            typeArguments.firstOrNull()?.toXType()
        }

        else -> null
    } ?: return null
    return XArrayTypeName(scope.settings, componentType, isNullable)
}

@PublishedApi
context(scope: PoetesseScope)
internal fun JPArrayTypeName.asXArrayTypeName(nullable: Boolean = false): XArrayTypeName =
    XArrayTypeName(scope.settings, componentType().toXType(), nullable)

fun XTypeName<*, *>.array(nullable: Boolean = false): XArrayTypeName =
    XArrayTypeName(settings, this, nullable)
