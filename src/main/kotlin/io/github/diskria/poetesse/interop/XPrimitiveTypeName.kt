package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.extensions.setBoxed
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.interop.XPrimitiveTypeName.Companion.J2KIND
import io.github.diskria.poetesse.interop.XPrimitiveTypeName.Companion.K2KIND
import io.github.diskria.poetesse.java.*
import io.github.diskria.poetesse.kotlin.*

class XPrimitiveTypeName internal constructor(
    override val settings: Poetesse.Settings,
    internal val kind: Kind,
    override val isBoxed: Boolean,
    override val isNullable: Boolean,
) : XTypedTypeName<KPClassName, JPTypeName>() {

    override fun interopToKotlinInternal(): KPClassName = kind.kotlin

    override fun interopToJavaInternal(): JPTypeName = kind.java.setBoxed(isBoxed)

    override fun boxInternal(): XPrimitiveTypeName = XPrimitiveTypeName(settings, kind, true, isNullable)

    enum class Kind(
        @PublishedApi internal val kotlin: KPClassName,
        @PublishedApi internal val java: JPTypeName,
    ) {
        BOOLEAN(KPBoolean, JPBoolean),
        BYTE(KPByte, JPByte),
        SHORT(KPShort, JPShort),
        INT(KPInt, JPInt),
        LONG(KPLong, JPLong),
        CHAR(KPChar, JPChar),
        FLOAT(KPFloat, JPFloat),
        DOUBLE(KPDouble, JPDouble);

        @PublishedApi
        internal val kotlinArray: KPClassName =
            KPClassName(kotlin.packageName, kotlin.simpleName + "Array")
    }

    internal companion object {
        val K2KIND: Map<KPClassName, Kind> = Kind.entries.associateBy { it.kotlin }
        val J2KIND: Map<JPTypeName, Kind> = Kind.entries.associateBy { it.java }
    }
}

@PublishedApi
context(scope: PoetesseScope)
internal fun KPTypeName.asXPrimitiveTypeNameOrNull(boxed: Boolean = isNullable): XPrimitiveTypeName? {
    val kind = K2KIND[setNullable(false).withoutAnnotations()] ?: return null
    return XPrimitiveTypeName(scope.settings, kind, isNullable || boxed, isNullable)
}

@PublishedApi
context(scope: PoetesseScope)
internal fun JPTypeName.asXPrimitiveTypeNameOrNull(nullable: Boolean = false): XPrimitiveTypeName? {
    val kind = J2KIND[setBoxed(false).withoutAnnotations()] ?: return null
    return XPrimitiveTypeName(scope.settings, kind, nullable || isBoxedPrimitive, nullable)
}
