package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass

typealias JavaStatementBuilder = JavaStatementScope.() -> String

@PoetesseJava
@JvmInline
value class JavaStatementScope private constructor(
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
        arguments += argument.statement
        return $$"$L"
    }

    fun T(argument: KClass<*>): String {
        arguments += argument.java
        return $$"$T"
    }

    inline fun <reified T> T(): String =
        T(T::class)

    fun T(argument: XClassName): String {
        arguments += argument.java
        return $$"$T"
    }

    internal companion object {
        fun of(build: JavaStatementBuilder): JavaDeferredCode =
            JavaStatementScope().let {
                JavaDeferredCode { JPCodeBlock.of(it.build(), *it.arguments.toTypedArray()) }
            }
    }
}
