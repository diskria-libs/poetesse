package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XTypeName
import kotlin.reflect.KClass

@PoetesseJava
open class JavaCodeArgumentsScope internal constructor(
    protected val arguments: MutableList<Any> = mutableListOf()
) : JavaCodeFactory {

    val expression: ExpressionScope by lazy { ExpressionScope() }

    private val prependBuffers: ArrayDeque<MutableList<Any>> = ArrayDeque()

    fun T(argument: XTypeName, interop: Boolean = true) = argument.toJava(interop).register('T')
    fun T(argument: KClass<out Any>, nullable: Boolean = false, interop: Boolean = true) =
        T(XTypeName.of(argument, nullable), interop)

    inline fun <reified T : Any> T(nullable: Boolean = false, interop: Boolean = true) =
        T(T::class, nullable, interop)

    fun S(argument: String) = argument.register('S')

    fun L(argument: Boolean) = argument.register()
    fun L(argument: Int) = argument.register()
    fun L(argument: String) = argument.register()
    protected fun L(argument: JPAnnotation) = argument.register()
    fun L(argument: JavaAnnotationRef) = L(argument.spec)
    fun L(argument: JavaCodeRef) = argument.codeBlock.register()
    fun L(build: JavaCodeBuilder) = L(code(build))

    protected fun <R> withPrepend(block: () -> R): R {
        val currentBuffer = mutableListOf<Any>()
        prependBuffers.addFirst(currentBuffer)
        return try {
            val result = block()
            val finishedBuffer = prependBuffers.removeFirst()
            val parentBuffer = prependBuffers.firstOrNull()
            (parentBuffer ?: arguments).addAll(0, finishedBuffer)
            result
        } catch (e: Throwable) {
            prependBuffers.removeFirstOrNull()
            throw e
        }
    }

    protected fun build(format: () -> String): JPCodeBlock =
        JPCodeBlock.of(format(), *arguments.toTypedArray())

    private fun <T : Any> T.register(mask: Char = 'L'): String {
        (prependBuffers.firstOrNull() ?: arguments) += this
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
}
