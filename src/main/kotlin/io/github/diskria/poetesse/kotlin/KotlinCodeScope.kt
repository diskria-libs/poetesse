package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
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

    val expression: ExpressionScope by lazy { ExpressionScope() }

    fun argument(mask: Char, argument: Any?): String {
        arguments += argument
        return "%$mask"
    }

    fun T(value: XTypeName) = argument('T', value.interopToKotlin())
    fun T(value: KClass<*>, nullable: Boolean = false) = T(xType(value, nullable = nullable))
    inline fun <reified T> T(nullable: Boolean = true) = T(T::class, nullable)
    inline fun <reified T : Any> T() = T<T>(nullable = false)

    fun S(value: String) = argument('S', value)

    fun L(value: Boolean) = argument('L', value)
    fun L(value: Int) = argument('L', value)
    fun L(value: String) = argument('L', value)

    fun L(value: KPAnnotation) = argument('L', value)
    fun L(value: KotlinAnnotationRef) = L(value.spec)

    fun L(codeBlock: KPCodeBlock) = argument('L', codeBlock)
    fun L(code: KotlinCodeRef) = L(code.codeBlock)
    fun L(block: Block) = L(code(block))

    inner class ExpressionScope {
        fun classLiteral(type: XTypeName): String =
            "${T(type)}::class"

        fun classLiteral(type: KClass<*>): String =
            classLiteral(xType(type))

        inline fun <reified T> classLiteral(): String =
            classLiteral(T::class)

        inline fun <reified E : Enum<E>> enumEntry(value: E): String =
            "${T<E>()}.${L(value.name)}"

        inline fun <reified E> arrayOf(values: Iterable<E>, crossinline transform: (E) -> String): String =
            values.joinToString(prefix = "[", postfix = "]") { transform(it) }

        fun concat(vararg elements: String): String =
            elements.joinToString(separator = " + ")
    }

    internal fun build() = KPCodeBlock.of(block(), *arguments.toTypedArray())

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(block: Block) = KotlinCodeRef {
            KotlinCodeScope(poetesse.config, block).build()
        }
    }
}
