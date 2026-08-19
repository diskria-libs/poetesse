package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.setBoxed
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.interop.XPrimitiveTypeName.Companion.javaToKind
import io.github.diskria.poetesse.interop.XPrimitiveTypeName.Companion.kotlinToKind
import io.github.diskria.poetesse.java.*
import io.github.diskria.poetesse.kotlin.*

class XPrimitiveTypeName private constructor(
    config: Poetesse.Config,
    internal val kind: Kind,
    override val isBoxed: Boolean,
    override val isNullable: Boolean,
) : XTypedTypeName<KPClassName, JPTypeName>(config) {

    override fun interopToKotlinInternal(): KPClassName = kind.kotlin

    override fun interopToJavaInternal(): JPTypeName = kind.java.setBoxed(isBoxed)

    override fun boxInternal() = of(kind, isBoxed = true, isNullable)

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
        internal val kotlinArray = KPClassName(kotlin.packageName, kotlin.simpleName + "Array")
    }

    internal companion object {
        val kotlinToKind: Map<KPClassName, Kind> = Kind.entries.associateBy { it.kotlin }
        val javaToKind: Map<JPTypeName, Kind> = Kind.entries.associateBy { it.java }

        context(poetesse: PoetesseScope)
        fun of(kind: Kind, isBoxed: Boolean, isNullable: Boolean = false) =
            XPrimitiveTypeName(poetesse.config, kind, isBoxed, isNullable)
    }
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPTypeName.asXPrimitiveTypeNameOrNull(boxed: Boolean): XPrimitiveTypeName? {
    val kind = kotlinToKind[setNullable(false).withoutAnnotations()] ?: return null
    return XPrimitiveTypeName.of(kind, isNullable || boxed, isNullable)
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPTypeName.asXPrimitiveTypeNameOrNull(nullable: Boolean): XPrimitiveTypeName? {
    val kind = javaToKind[setBoxed(false).withoutAnnotations()] ?: return null
    return XPrimitiveTypeName.of(kind, nullable || isBoxedPrimitive, nullable)
}
