package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XTypeName
import kotlin.reflect.KClass

typealias JavaCodeBuilder = JavaCodeScope.() -> String

@PoetesseJava
class JavaCodeScope internal constructor(
    private val block: JavaCodeBuilder,
    private val arguments: MutableList<Any?> = mutableListOf(),
) : JavaCodeFactory {

    val expression: ExpressionScope by lazy { ExpressionScope() }

    fun T(argument: XTypeName, interop: Boolean = true) = argument.toJava(interop).registerArgument('T')

    fun T(argument: KClass<out Any>, nullable: Boolean = false, interop: Boolean = true) =
        T(XTypeName.of(argument, nullable), interop)

    inline fun <reified T : Any> T(nullable: Boolean = false, interop: Boolean = true) =
        T(T::class, nullable, interop)

    fun S(argument: CharSequence) = argument.registerArgument('S')

    fun L(argument: Boolean) = argument.registerArgument()
    fun L(argument: Int) = argument.registerArgument()
    fun L(argument: String) = argument.registerArgument()
    fun L(argument: JavaAnnotationRef) = L(argument.spec)
    fun L(argument: JavaCodeRef) = argument.codeBlock.registerArgument()
    fun L(build: JavaCodeBuilder) = L(code(build))

    fun argument(mask: Char, argument: Any?) = argument.registerArgument(mask)

    internal fun L(argument: JPAnnotation) = argument.registerArgument()

    private fun <T> T.registerArgument(mask: Char = 'L'): String {
        arguments += this
        return "$$mask"
    }

    inner class ExpressionScope {

        fun classRef(value: XTypeName, interop: Boolean = true): String =
            "${T(value, interop)}.class"

        fun classRef(value: KClass<out Any>, interop: Boolean = true): String =
            classRef(XTypeName.of(value), interop)

        inline fun <reified T : Any> classRef(interop: Boolean = true): String =
            classRef(T::class, interop)

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
        fun of(block: JavaCodeBuilder): JavaCodeRef =
            JavaCodeRef { JavaCodeScope(block).build() }
    }
}
