package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

typealias JavaCodeBuilder = JavaCodeScope.() -> String

@PoetesseJava
class JavaCodeScope internal constructor(
    override val settings: Poetesse.Settings,
    private val block: JavaCodeBuilder,
    private val arguments: MutableList<Any?> = mutableListOf(),
) : JavaCodeFactory {

    val expression: ExpressionScope by lazy { ExpressionScope() }

    fun argument(mask: Char, argument: Any?): String {
        arguments += argument
        return "$$mask"
    }

    fun T(value: XTypeName<*, *>, resolveNullability: Boolean = false) =
        argument('T', value.interopToJava(resolveNullability = resolveNullability))

    fun T(value: KClass<*>, nullable: Boolean = false, resolveNullability: Boolean = false) =
        T(xType(value, nullable = nullable), resolveNullability)

    inline fun <reified T> T(nullable: Boolean = true, resolveNullability: Boolean = false) =
        T(T::class, nullable, resolveNullability)

    inline fun <reified T : Any> T(resolveNullability: Boolean = false) =
        T<T>(nullable = false, resolveNullability = resolveNullability)

    fun S(value: CharSequence) = argument('S', value)

    fun L(value: Boolean) = argument('L', value)
    fun L(value: Int) = argument('L', value)
    fun L(value: String) = argument('L', value)

    fun L(value: JPAnnotation) = argument('L', value)
    fun L(value: JavaAnnotationRef) = L(value.spec)

    fun L(codeBlock: JPCodeBlock) = argument('L', codeBlock)
    fun L(code: JavaCodeRef) = L(code.codeBlock)
    fun L(build: JavaCodeBuilder) = L(code(build))

    inner class ExpressionScope {

        fun classLiteral(type: XTypeName<*, *>): String =
            "${T(type)}.class"

        fun classLiteral(type: KClass<*>): String =
            classLiteral(xType(type))

        inline fun <reified T> classLiteral(): String =
            classLiteral(T::class)

        inline fun <reified E : Enum<E>> enumEntry(value: E): String =
            "${T<E>()}.${L(value.name)}"

        inline fun <reified E> arrayOf(values: Iterable<E>, crossinline transform: (E) -> String): String =
            values.joinToString(prefix = "{", postfix = "}") { transform(it) }

        fun concat(vararg elements: String): String =
            elements.joinToString(separator = " + ")
    }

    internal fun build(): JPCodeBlock =
        JPCodeBlock.of(block(), *arguments.toTypedArray())

    internal companion object {
        fun of(settings: Poetesse.Settings, block: JavaCodeBuilder): JavaCodeRef =
            JavaCodeRef { JavaCodeScope(settings, block).build() }
    }
}
