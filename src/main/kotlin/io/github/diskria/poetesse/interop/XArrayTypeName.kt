package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.diskria.poetesse.java.JPArrayTypeName
import io.github.diskria.poetesse.kotlin.KPArray
import io.github.diskria.poetesse.kotlin.KPClassName
import io.github.diskria.poetesse.kotlin.KPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

class XArrayTypeName internal constructor(
    val componentType: XTypeName,
    override val nullable: Boolean = false,
) : XTypeName() {

    override fun interopToKotlin(): KPTypeName =
        if (componentType is XPrimitiveTypeName) {
            componentType.kind.kotlinArrayClassName
        } else {
            KPArray.parameterizedBy(componentType.interopToKotlin())
        }

    override fun interopToJava(): JPArrayTypeName =
        JPArrayTypeName.of(componentType.interopToJava())

    override fun setNullableInternal(nullable: Boolean): XArrayTypeName =
        XArrayTypeName(componentType, nullable)

    companion object {
        fun of(type: KClass<out Any>): XArrayTypeName =
            type.asXTypeName().wrapToArray()

        inline fun <reified T : Any> of(): XArrayTypeName =
            of(T::class)
    }
}

fun KPTypeName.asXArrayTypeNameOrNull(): XArrayTypeName? = when (this) {
    is KPClassName -> {
        XPrimitiveKind.entries.find { it.kotlinArrayClassName == this }
            ?.java?.asXPrimitiveTypeName()
            ?.let { XArrayTypeName(it, isNullable) }
    }

    is KPParameterizedTypeName -> {
        if (rawType == com.squareup.kotlinpoet.ARRAY) {
            typeArguments.singleOrNull()?.asXTypeName()?.let { XArrayTypeName(it, isNullable) }
        } else {
            null
        }
    }

    else -> null
}

fun JPArrayTypeName.asXArrayTypeName(): XArrayTypeName =
    XArrayTypeName(componentType().asXTypeName())

fun XTypeName.wrapToArray(): XArrayTypeName =
    XArrayTypeName(this)
