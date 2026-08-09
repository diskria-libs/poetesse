package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.extensions.setBoxed
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.interop.XPrimitiveTypeName.Companion.J2KIND
import io.github.diskria.poetesse.interop.XPrimitiveTypeName.Companion.K2KIND
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.kotlin.KPClassName
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

class XPrimitiveTypeName internal constructor(
    internal val kind: XPrimitiveKind,
    override val isNullable: Boolean = false,
) : XTypeName() {

    override fun interopToKotlin(): KPClassName =
        kind.kotlin.setNullable(isNullable)

    override fun interopToJava(): JPTypeName =
        kind.java.setBoxed(isNullable)

    override fun setNullable(nullable: Boolean): XPrimitiveTypeName =
        XPrimitiveTypeName(kind, nullable)

    fun box(): XPrimitiveTypeName =
        setNullable(true)

    fun unbox(): XPrimitiveTypeName =
        setNullable(false)

    companion object {
        internal val K2KIND: Map<KPClassName, XPrimitiveKind> = XPrimitiveKind.entries.associateBy { it.kotlin }
        internal val J2KIND: Map<JPTypeName, XPrimitiveKind> = XPrimitiveKind.entries.associateBy { it.java }
    }
}

fun JPTypeName.asXPrimitiveTypeNameOrNull(): XPrimitiveTypeName? {
    val kind = J2KIND[setBoxed(false).withoutAnnotations()] ?: return null
    return XPrimitiveTypeName(kind, isBoxedPrimitive)
}

fun JPTypeName.asXPrimitiveTypeName(): XPrimitiveTypeName =
    requireNotNull(asXPrimitiveTypeNameOrNull()) { "$this is not a primitive type" }

fun KPTypeName.asXPrimitiveTypeNameOrNull(): XPrimitiveTypeName? {
    val kind = K2KIND[setNullable(false).withoutAnnotations()] ?: return null
    return XPrimitiveTypeName(kind, isNullable)
}

fun KPTypeName.asXPrimitiveTypeName(): XPrimitiveTypeName =
    requireNotNull(asXPrimitiveTypeNameOrNull()) { "$this is not a primitive type" }

fun KClass<*>.asXPrimitiveTypeNameOrNull(nullable: Boolean = false): XPrimitiveTypeName? =
    asClassName().setNullable(nullable).asXPrimitiveTypeNameOrNull()

fun KClass<*>.asXPrimitiveTypeName(nullable: Boolean = false): XPrimitiveTypeName =
    asClassName().setNullable(nullable).asXPrimitiveTypeName()
