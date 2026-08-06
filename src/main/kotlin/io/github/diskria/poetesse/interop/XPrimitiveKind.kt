package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.java.*
import io.github.diskria.poetesse.kotlin.*

internal enum class XPrimitiveKind(
    internal val kotlin: KPClassName,
    internal val java: JPTypeName,
) {
    BOOLEAN(KPBoolean, JPBoolean),
    BYTE(KPByte, JPByte),
    SHORT(KPShort, JPShort),
    INT(KPInt, JPInt),
    LONG(KPLong, JPLong),
    CHAR(KPChar, JPChar),
    FLOAT(KPFloat, JPFloat),
    DOUBLE(KPDouble, JPDouble);

    internal val kotlinArrayClassName: KPClassName = kotlin.peerClass(kotlin.simpleName + "Array")
}
