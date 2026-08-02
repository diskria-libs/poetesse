package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XTypeName
import kotlin.reflect.KClass

@PoetesseJava
open class JavaCodeArgumentsScope internal constructor(
    protected val arguments: MutableList<Any> = mutableListOf()
) : JavaCodeFactory {

    val expression: ExpressionScope by lazy { ExpressionScope() }

    fun S(argument: String): String {
        arguments += argument
        return $$"$S"
    }

    fun L(argument: Boolean): String {
        arguments += argument
        return $$"$L"
    }

    fun L(argument: Int): String {
        arguments += argument
        return $$"$L"
    }

    fun L(argument: String): String {
        arguments += argument
        return $$"$L"
    }

    fun L(argument: JavaAnnotationRef): String {
        arguments += argument.spec
        return $$"$L"
    }

    fun L(argument: JavaCodeRef): String {
        arguments += argument.codeBlock
        return $$"$L"
    }

    fun L(build: JavaCodeBuilder): String =
        L(code(build))

    fun T(argument: XTypeName, interop: Boolean = true): String {
        arguments += argument.toJava(interop)
        return $$"$T"
    }

    fun T(argument: KClass<out Any>, nullable: Boolean = false, interop: Boolean = true): String =
        T(XTypeName.of(argument, nullable), interop)

    inline fun <reified T : Any> T(nullable: Boolean = false, interop: Boolean = true): String =
        T(T::class, nullable, interop)

    protected fun build(format: () -> String): JPCodeBlock =
        JPCodeBlock.of(format(), *arguments.toTypedArray())

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
}
