package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.kotlin.KPClassName

internal enum class XPrimitiveKind(
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
    DOUBLE(com.squareup.kotlinpoet.DOUBLE, JPTypeName.DOUBLE);

    internal val kotlinArrayClassName: KPClassName = kotlin.peerClass(kotlin.simpleName + "Array")
}
