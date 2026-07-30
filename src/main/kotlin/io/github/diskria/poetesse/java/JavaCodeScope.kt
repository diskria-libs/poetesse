package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass

typealias JavaCodeBuilder = JavaCodeScope.() -> String

@PoetesseJava
@JvmInline
value class JavaCodeScope private constructor(
    private val arguments: MutableList<Any> = mutableListOf()
) : JavaCodeFactory {

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

    fun L(argument: JavaDeferredAnnotation<*>): String {
        arguments += argument.spec
        return $$"$L"
    }

    fun L(argument: JavaDeferredCode): String {
        arguments += argument.codeBlock
        return $$"$L"
    }

    fun T(argument: XClassName): String {
        arguments += argument.java
        return $$"$T"
    }

    fun T(argument: KClass<*>): String {
        arguments += argument.java
        return $$"$T"
    }

    inline fun <reified T> T(): String =
        T(T::class)

    internal companion object {
        fun of(buildCode: JavaCodeBuilder): JavaDeferredCode =
            JavaCodeScope().let {
                JavaDeferredCode { JPCodeBlock.of(it.buildCode(), *it.arguments.toTypedArray()) }
            }
    }
}

fun JavaCodeScope.classReference(value: XClassName): String =
    "${T(value)}.class"

fun JavaCodeScope.classReference(value: KClass<*>): String =
    "${T(value)}.class"

inline fun <reified T> JavaCodeScope.classReference(): String =
    "${T<T>()}.class"

inline fun <reified E : Enum<E>> JavaCodeScope.enumEntryReference(value: E): String =
    "${T<E>()}.${L(value.name)}"
