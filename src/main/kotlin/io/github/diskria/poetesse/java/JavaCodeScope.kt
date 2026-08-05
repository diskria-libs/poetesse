package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXTypeName
import io.github.diskria.poetesse.interop.setNullable
import kotlin.reflect.KClass

typealias JavaCodeBuilder = JavaCodeScope.() -> String

@PoetesseJava
class JavaCodeScope internal constructor(
    private val block: JavaCodeBuilder,
    private val arguments: MutableList<Any?> = mutableListOf(),
) : JavaCodeFactory {

    val expression: ExpressionScope by lazy { ExpressionScope() }

    fun argument(argument: Any?, mask: Char): String {
        arguments += argument
        return "$$mask"
    }

    fun T(value: XTypeName, interop: Boolean = true) = argument(value.toJava(interop), 'T')

    fun T(value: KClass<out Any>, nullable: Boolean = false, interop: Boolean = true) =
        T(value.asXTypeName().setNullable(nullable), interop)

    inline fun <reified T : Any> T(nullable: Boolean = false, interop: Boolean = true) =
        T(T::class, nullable, interop)

    fun S(value: CharSequence) = argument(value, 'S')

    fun L(value: Boolean) = argument(value, 'L')
    fun L(value: Int) = argument(value, 'L')
    fun L(value: String) = argument(value, 'L')

    fun L(value: JPAnnotation) = argument(value, 'L')
    fun L(value: JavaAnnotationRef) = L(value.spec)

    fun L(code: JavaCodeRef) = argument(code.codeBlock, 'L')
    fun L(build: JavaCodeBuilder) = L(code(build))

    inner class ExpressionScope {

        fun classRef(value: XTypeName, interop: Boolean = true): String =
            "${T(value, interop)}.class"

        fun classRef(type: KClass<out Any>, interop: Boolean = true): String =
            classRef(type.asXTypeName(), interop)

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
