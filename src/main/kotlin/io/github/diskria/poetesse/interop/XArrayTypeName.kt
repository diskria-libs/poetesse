package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.java.JPArrayTypeName
import io.github.diskria.poetesse.kotlin.KPArray
import io.github.diskria.poetesse.kotlin.KPClassName
import io.github.diskria.poetesse.kotlin.KPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

class XArrayTypeName internal constructor(
    val componentType: XTypeName,
    override val isNullable: Boolean = false,
) : XTypeName() {

    override fun interopToKotlin(): KPTypeName =
        if (componentType is XPrimitiveTypeName && !componentType.isNullable) {
            componentType.kind.kotlinArrayClassName
        } else {
            KPArray.parameterizedBy(componentType.interopToKotlin())
        }.setNullable(isNullable)

    override fun interopToJava(): JPArrayTypeName =
        JPArrayTypeName.of(componentType.interopToJava())

    override fun setNullable(nullable: Boolean): XArrayTypeName =
        XArrayTypeName(componentType, nullable)
}

fun KPTypeName.asXArrayTypeNameOrNull(): XArrayTypeName? = when (this) {
    is KPClassName -> {
        XPrimitiveKind.entries.find { it.kotlinArrayClassName == this }
            ?.java?.asXPrimitiveTypeName()
            ?.let { XArrayTypeName(it, isNullable) }
    }

    is KPParameterizedTypeName -> {
        if (rawType == KPArray) {
            typeArguments.singleOrNull()?.asXTypeName()?.let { XArrayTypeName(it, isNullable) }
        } else {
            null
        }
    }

    else -> null
}

fun KClass<*>.asXArrayTypeNameOrNull(nullable: Boolean = false): XArrayTypeName? =
    asClassName().setNullable(nullable).asXArrayTypeNameOrNull()

fun JPArrayTypeName.asXArrayTypeName(): XArrayTypeName =
    XArrayTypeName(componentType().asXTypeName())

fun XTypeName.array(): XArrayTypeName =
    XArrayTypeName(this)
