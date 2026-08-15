package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.interop.XArrayTypeName.Companion.kotlinPrimitiveArrays
import io.github.diskria.poetesse.java.JPArrayTypeName
import io.github.diskria.poetesse.kotlin.KPArray
import io.github.diskria.poetesse.kotlin.KPClassName
import io.github.diskria.poetesse.kotlin.KPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPTypeName

class XArrayTypeName private constructor(
    config: Poetesse.Config,
    val componentType: XTypeName,
    override val isNullable: Boolean,
) : XTypedTypeName<KPTypeName, JPArrayTypeName>(config) {

    override fun interopToKotlinInternal(): KPTypeName =
        if (componentType is XPrimitiveTypeName && !componentType.isBoxed) {
            componentType.kind.kotlinArray
        } else {
            KPArray.parameterizedBy(componentType.interopToKotlin())
        }

    override fun interopToJavaInternal(): JPArrayTypeName =
        JPArrayTypeName.of(componentType.interopToJava())

    internal companion object {
        val kotlinPrimitiveArrays: Map<KPClassName, KPClassName> =
            XPrimitiveTypeName.Kind.entries.associate { it.kotlinArray to it.kotlin }

        context(poetesse: PoetesseScope)
        fun of(componentType: XTypeName, isNullable: Boolean) =
            XArrayTypeName(poetesse.config, componentType, isNullable)
    }
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPTypeName.asXArrayTypeNameOrNull(): XArrayTypeName? {
    val componentType = when (this) {
        is KPClassName -> kotlinPrimitiveArrays[this]?.asX<XPrimitiveTypeName>()
        is KPParameterizedTypeName if (rawType.setNullable(false).withoutAnnotations() == KPArray) -> with(poetesse) {
            typeArguments.firstOrNull()?.let { xType(it) }
        }

        else -> null
    } ?: return null
    return XArrayTypeName.of(componentType, isNullable)
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPArrayTypeName.asXArrayTypeName(nullable: Boolean = false) = with(poetesse) {
    XArrayTypeName.of(xType(componentType()), nullable)
}

fun XTypeName.array(nullable: Boolean = false) = XArrayTypeName.of(this, nullable)
