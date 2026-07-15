package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass

internal typealias JavaStatementBuilder = JavaStatementScope.() -> String

@PoetesseJava
@JvmInline
value class JavaStatementScope private constructor(private val arguments: MutableList<Any> = mutableListOf()) {

    fun S(argument: String): String {
        arguments += argument
        return $$"$S"
    }

    fun L(argument: Any): String {
        arguments += argument
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
        fun create(statement: JavaStatementBuilder): JPCodeBlock =
            JavaStatementScope().let {
                JPCodeBlock.of(it.statement(), *it.arguments.toTypedArray())
            }
    }
}
