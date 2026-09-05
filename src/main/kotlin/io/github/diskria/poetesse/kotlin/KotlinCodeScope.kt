package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.appendEscapedChar
import io.github.diskria.poetesse.extensions.toCodeString
import io.github.diskria.poetesse.extensions.unicodeEscaped
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

class KotlinCodeScope private constructor(
    override val config: Poetesse.Config,
    private val block: Block,
    private val arguments: MutableList<Any?> = mutableListOf(),
) : PoetesseKotlinScope,
    KotlinCodeFactory {

    internal typealias Block = KotlinCodeScope.() -> String

    fun argument(mask: Char, argument: Any?): String {
        arguments += argument
        return "%$mask"
    }

    fun T(value: XTypeName) = argument('T', value.interopToKotlin())
    fun T(value: KClass<*>, nullable: Boolean = false) = T(xType(value, nullable = nullable))
    inline fun <reified T> T(nullable: Boolean = true) = T(T::class, nullable)
    inline fun <reified T : Any> T() = T<T>(nullable = false)

    fun S(value: String, dollars: Int = 1, block: StringScope.() -> Unit = {}): String {
        val scope = StringScope().apply(block)
        return L(buildStringLiteral(value, dollars, scope.isRaw, scope.multilineTrim))
    }

    fun L(value: Boolean) = argument('L', value)
    fun L(value: Byte) = L(value.toCodeString())
    fun L(value: Short) = argument('L', value)
    fun L(value: Int) = argument('L', value)
    fun L(value: Long) = L("${value}L")
    fun L(value: Char) = L(value.toCodeString())
    fun L(value: Float) = L("${value}f")
    fun L(value: Double) = argument('L', value)
    fun L(value: String) = argument('L', value)
    fun L(value: KPAnnotation) = argument('L', value)
    fun L(value: KotlinAnnotationRef) = L(value.spec)
    fun L(codeBlock: KPCodeBlock) = argument('L', codeBlock)
    fun L(code: KotlinCodeRef) = L(code.codeBlock)
    fun L(block: Block) = L(code(block))

    internal fun build() = KPCodeBlock.of(block(), *arguments.toTypedArray())

    private fun buildStringLiteral(
        value: String,
        dollars: Int,
        raw: Boolean,
        multilineTrim: Poetesse.StringTrim,
    ): String {
        val prefixDollars = if (dollars > 1) dollars else 0
        val quotes = if (raw) 3 else 1
        return buildString(prefixDollars + quotes + value.length + 30 + quotes) {
            if (prefixDollars > 0) append("$".repeat(dollars))
            repeat(quotes) { append('"') }
            val stringStart = length
            var multiline = false
            var index = 0
            while (index < value.length) {
                val char = value[index]
                if (raw) {
                    if (char == '"') {
                        index = appendRawQuotes(value, index, dollars)
                        continue
                    }
                    if (char == '\n') {
                        val lineBreak = "\n${multilineTrim.linePrefix}"
                        append(lineBreak)
                        if (!multiline) {
                            insert(stringStart, lineBreak)
                            multiline = true
                        }
                        index++
                        continue
                    }
                }
                if (char == '$') {
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
                if (char.isSegmentSpecial()) {
                    if (raw) {
                        repeat(dollars) { append('$') }
                        append("{'${char.unicodeEscaped()}'}")
                    } else {
                        append(char.unicodeEscaped())
                    }
                } else {
                    if (raw) {
                        append(char)
                    } else {
                        appendEscapedChar(char, quoteToEscape = '"')
                    }
                }
                index++
            }
            if (raw && multiline && !value.endsWith('\n')) append('\n')
            repeat(quotes) { append('"') }
            if (raw && multiline) append(multilineTrim.postProcess)
        }
    }

    inner class StringScope internal constructor(
        override val config: Poetesse.Config = this@KotlinCodeScope.config,
    ) : PoetesseKotlinScope {

        internal var isRaw: Boolean = false
        internal var multilineTrim: Poetesse.StringTrim = config.defaultRawMultilineTrim

        fun raw(multilineTrim: Poetesse.StringTrim = config.defaultRawMultilineTrim, enabled: Boolean = true) {
            isRaw = enabled
            this.multilineTrim = multilineTrim
        }

        fun trimMargin() = Poetesse.StringTrim.Default
        fun trimMargin(prefix: String) = Poetesse.StringTrim.Margin(prefix)
        fun trimIndent() = Poetesse.StringTrim.Indent
        fun noTrim() = Poetesse.StringTrim.None
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(block: Block) = KotlinCodeRef {
            KotlinCodeScope(poetesse.config, block).build()
        }
    }
}

private inline fun StringBuilder.appendWhile(value: String, startIndex: Int, predicate: (Char) -> Boolean): Int {
    var index = startIndex
    while (index < value.length && predicate(value[index])) {
        append(value[index++])
    }
    return index
}

private fun StringBuilder.appendRawQuotes(value: String, startIndex: Int, dollars: Int): Int {
    var index = startIndex
    var quoteCount = 0
    while (index < value.length && value[index] == '"') {
        quoteCount++
        index++
        if (quoteCount == 3) {
            repeat(dollars) { append('$') }
            append("{'\"'}")
            quoteCount = 0
        } else {
            append('"')
        }
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
        if (char.isSegmentSpecial()) {
            append(char.unicodeEscaped())
        } else if (char == '\n') {
            append("\\n")
        } else {
            append(char)
        }
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

private fun Char.isSegmentSpecial(): Boolean =
    this == '♢' || this == '·'
