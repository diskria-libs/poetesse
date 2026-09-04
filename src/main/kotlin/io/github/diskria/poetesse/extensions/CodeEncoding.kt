package io.github.diskria.poetesse.extensions

import io.github.diskria.poetesse.interop.PoetesseScope

private val byteHexFormat = HexFormat {
    upperCase = true
    number { prefix = "0x" }
}

fun Byte.toCodeString(): String =
    toHexString(byteHexFormat)

fun StringBuilder.appendEscapedChar(char: Char, quoteToEscape: Char?, isJava: Boolean) {
    when (char) {
        '\b' -> append("""\b""")
        '\t' -> append("""\t""")
        '\n' -> append("""\n""")
        12.toChar() if isJava -> append("""\f""")
        '\r' -> append("""\r""")
        '\\' -> append("""\\""")
        quoteToEscape -> append("""\$quoteToEscape""")
        else -> {
            if (char.isISOControl()) append(String.format("\\u%04x", char.code))
            else append(char)
        }
    }
}

fun Char.toCodeString(isJava: Boolean = false): String = buildString(8) {
    append('\'')
    appendEscapedChar(this@toCodeString, quoteToEscape = '\'', isJava)
    append('\'')
}

context(poetesse: PoetesseScope)
fun StringBuilder.appendCodeString(value: String, dollars: Int = 0, raw: Boolean = false, isJava: Boolean = false) {
    val explicitDollars = if (dollars > 1) dollars else 0
    val quotes = if (raw) 3 else 1
    repeat(explicitDollars) { append('$') }
    repeat(quotes) { append('"') }
    var index = 0
    while (index < value.length) {
        val char = value[index]
        if (!isJava && char == '$') {
            val startDollarIndex = index
            index = appendWhile(value, index) { it == '$' }
            val dollarCount = index - startDollarIndex
            if (dollarCount == dollars && index < value.length) {
                if (value[index] == '{') {
                    index = appendExpression(value, index)
                } else if (value[index].isJavaIdentifierStart()) {
                    index = appendWhile(value, index) { it.isJavaIdentifierPart() }
                }
            }
            continue
        }
        if (raw) {
            append(char)
        } else {
            appendEscapedChar(char, quoteToEscape = '"', isJava)
            if (char == '\n' && index < value.lastIndex) {
                append("\" +\n\"")
            }
        }
        index++
    }
    repeat(quotes) { append('"') }
}

private inline fun StringBuilder.appendWhile(value: String, startIndex: Int, predicate: (Char) -> Boolean): Int {
    var index = startIndex
    while (index < value.length && predicate(value[index])) {
        append(value[index++])
    }
    return index
}

private fun StringBuilder.appendExpression(value: String, startIndex: Int): Int {
    var index = startIndex
    var depth = 0
    var inString = false
    var inChar = false
    var isEscape = false
    while (index < value.length) {
        val char = value[index++]
        append(char)
        if (isEscape) {
            isEscape = false
            continue
        }
        if (!inChar && char == '"') {
            inString = !inString
            continue
        }
        if (!inString && char == '\'') {
            inChar = !inChar
            continue
        }
        if (inString || inChar) {
            if (char == '\\') {
                isEscape = true
                continue
            }
        } else {
            when (char) {
                '{' -> depth++
                '}' -> if (--depth == 0) break
            }
        }
    }
    return index
}
