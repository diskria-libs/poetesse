package io.github.diskria.poetesse.extensions

private val byteHexFormat = HexFormat {
    upperCase = true
    number { prefix = "0x" }
}

fun Byte.toCodeString(): String =
    toHexString(byteHexFormat)

fun Char.toCodeString(): String = when (this) {
    '\n' -> """'\n'"""
    '\r' -> """'\r'"""
    '\t' -> """'\t'"""
    '\'' -> """'\''"""
    '\\' -> """'\\'"""
    else -> when (code) {
        in 32..126 -> "'$this'"
        else -> String.format("'\\u%04X'", code)
    }
}
