package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

class JavaCodeScope private constructor(
    override val config: Poetesse.Config,
    private val block: Block,
    private val arguments: MutableList<Any?> = mutableListOf(),
) : JavaCodeFactory {

    internal typealias Block = JavaCodeScope.() -> String

    val expression: ExpressionScope by lazy { ExpressionScope() }

    fun argument(mask: Char, argument: Any?): String {
        arguments += argument
        return "$$mask"
    }

    fun T(value: XTypeName, resolveNullability: Boolean = false) =
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
    fun L(block: Block) = L(code(block))

    inner class ExpressionScope {

        fun classLiteral(type: XTypeName): String =
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

    internal fun build() = JPCodeBlock.of(block(), *arguments.toTypedArray())

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(block: Block) = JavaCodeRef {
            JavaCodeScope(scope.config, block).build()
        }
    }
}
