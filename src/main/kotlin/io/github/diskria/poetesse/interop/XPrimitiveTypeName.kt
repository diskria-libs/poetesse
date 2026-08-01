package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.kotlin.KPClassName
import kotlin.reflect.KClass

class XPrimitiveTypeName private constructor(
    val kind: XPrimitiveKind,
    override val isNullable: Boolean,
) : XTypeName {

    override val kotlinAsJava: JPClassName =
        XClassName.of(kind.kotlin).kotlinAsJava

    override fun interopToKotlin(): KPClassName =
        kind.kotlin.setNullable(isNullable)

    override fun interopToJava(): JPTypeName =
        if (isNullable) kind.java.box()
        else kind.java

    companion object {
        private val K2KIND: Map<KPClassName, XPrimitiveKind> =
            XPrimitiveKind.entries.associateBy { it.kotlin }

        private val J2KIND: Map<JPTypeName, XPrimitiveKind> = buildMap {
            XPrimitiveKind.entries.forEach { kind ->
                put(kind.java, kind)
                put(kind.java.box(), kind)
            }
        }

        fun of(kind: XPrimitiveKind, isNullable: Boolean = false): XPrimitiveTypeName =
            XPrimitiveTypeName(kind, isNullable)

        fun ofOrNull(kotlin: KPClassName): XPrimitiveTypeName? {
            val key = kotlin.setNullable(false)
            val kind = K2KIND[key] ?: return null
            return XPrimitiveTypeName(kind, kotlin.isNullable)
        }

        fun of(kotlin: KPClassName): XPrimitiveTypeName =
            requireNotNull(ofOrNull(kotlin)) {
                "$kotlin is not a primitive Kotlin type"
            }

        fun of(java: JPTypeName): XPrimitiveTypeName {
            val kind = requireNotNull(J2KIND[java]) {
                "$java is not a primitive or boxed Java type"
            }
            val isNullable = (java != kind.java)
            return XPrimitiveTypeName(kind, isNullable)
        }

        fun ofOrNull(kClass: KClass<out Any>, isNullable: Boolean = false): XPrimitiveTypeName? =
            ofOrNull(kClass.asClassName().setNullable(isNullable))

        inline fun <reified T : Any> ofOrNull(isNullable: Boolean = false): XPrimitiveTypeName? =
            ofOrNull(T::class, isNullable)
    }
}

enum class XPrimitiveKind(
    internal val kotlin: KPClassName,
    internal val java: JPTypeName,
) {
    BOOLEAN(com.squareup.kotlinpoet.BOOLEAN, JPTypeName.BOOLEAN),
    BYTE(com.squareup.kotlinpoet.BYTE, JPTypeName.BYTE),
    SHORT(com.squareup.kotlinpoet.SHORT, JPTypeName.SHORT),
    INT(com.squareup.kotlinpoet.INT, JPTypeName.INT),
    LONG(com.squareup.kotlinpoet.LONG, JPTypeName.LONG),
    CHAR(com.squareup.kotlinpoet.CHAR, JPTypeName.CHAR),
    FLOAT(com.squareup.kotlinpoet.FLOAT, JPTypeName.FLOAT),
    DOUBLE(com.squareup.kotlinpoet.DOUBLE, JPTypeName.DOUBLE),
    VOID(com.squareup.kotlinpoet.UNIT, JPTypeName.VOID),
}
