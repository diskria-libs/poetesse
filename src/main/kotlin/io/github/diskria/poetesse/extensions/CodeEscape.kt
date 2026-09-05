package io.github.diskria.poetesse.extensions

private val byteHexFormat = HexFormat {
    upperCase = true
    number { prefix = "0x" }
}

internal fun Byte.toCodeString(): String =
    toHexString(byteHexFormat)

internal fun Char.unicodeEscaped(): String = "\\u${code.toString(16).padStart(4, '0')}"

internal fun StringBuilder.appendEscapedChar(char: Char, quoteToEscape: Char?, isJava: Boolean = false) {
    when (char) {
        '\b' -> append("""\b""")
        '\t' -> append("""\t""")
        '\n' -> append("""\n""")
        '' if isJava -> append("""\f""")
        '\r' -> append("""\r""")
        '\\' -> append("""\\""")
        quoteToEscape -> append("""\$quoteToEscape""")
        else -> {
            if (char.isISOControl()) append(char.unicodeEscaped())
            else append(char)
        }
    }
}

internal fun Char.toCodeString(isJava: Boolean = false): String =
    buildString(8) {
        append('\'')
        appendEscapedChar(this@toCodeString, quoteToEscape = '\'', isJava)
        append('\'')
    }
