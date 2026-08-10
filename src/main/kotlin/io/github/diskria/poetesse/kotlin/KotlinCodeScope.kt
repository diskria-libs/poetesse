package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

typealias KotlinCodeBuilder = KotlinCodeScope.() -> String

@PoetesseKotlin
class KotlinCodeScope internal constructor(
    override val settings: Poetesse.Settings,
    private val block: KotlinCodeBuilder,
    private val arguments: MutableList<Any?> = mutableListOf(),
) : KotlinCodeFactory {

    val expression: ExpressionScope by lazy { ExpressionScope() }

    fun argument(mask: Char, argument: Any?): String {
        arguments += argument
        return "%$mask"
    }

    fun T(value: XTypeName<*, *>) = argument('T', value.interopToKotlin())
    fun T(value: KClass<*>, nullable: Boolean = false) = T(xType(value, nullable = nullable))
    inline fun <reified T> T(nullable: Boolean = true) = T(T::class, nullable)
    inline fun <reified T : Any> T() = T<T>(nullable = false)

    fun S(value: CharSequence) = argument('S', value)

    fun L(value: Boolean) = argument('L', value)
    fun L(value: Int) = argument('L', value)
    fun L(value: String) = argument('L', value)

    fun L(value: KPAnnotation) = argument('L', value)
    fun L(value: KotlinAnnotationRef) = L(value.spec)

    fun L(code: KotlinCodeRef) = argument('L', code.codeBlock)
    fun L(build: KotlinCodeBuilder) = L(code(build))

    inner class ExpressionScope {
        fun classLiteral(type: XTypeName<*, *>): String =
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

    internal fun build(): KPCodeBlock =
        KPCodeBlock.of(block(), *arguments.toTypedArray())

    internal companion object {
        fun of(settings: Poetesse.Settings, block: KotlinCodeBuilder): KotlinCodeRef =
            KotlinCodeRef { KotlinCodeScope(settings, block).build() }
    }
}
