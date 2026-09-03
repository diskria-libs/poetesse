package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.appendCodeString
import io.github.diskria.poetesse.extensions.toCodeString
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

class JavaCodeScope private constructor(
    override val config: Poetesse.Config,
    private val block: Block,
    private val arguments: MutableList<Any?> = mutableListOf(),
) : PoetesseJavaScope,
    JavaCodeFactory {

    internal typealias Block = JavaCodeScope.() -> String

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

    fun S(value: String): String =
        L(buildString(value.length + 32) {
            appendCodeString(value, dollars = 0, raw = false, isJava = true)
        })

    fun L(value: Boolean) = argument('L', value)
    fun L(value: Byte) = L(value.toCodeString())
    fun L(value: Short) = argument('L', value)
    fun L(value: Int) = argument('L', value)
    fun L(value: Long) = L("${value}L")
    fun L(value: Char) = L(value.toCodeString(isJava = true))
    fun L(value: Float) = L("${value}f")
    fun L(value: Double) = argument('L', value)
    fun L(value: String) = argument('L', value)
    fun L(value: JPAnnotation) = argument('L', value)
    fun L(value: JavaAnnotationRef) = L(value.spec)
    fun L(codeBlock: JPCodeBlock) = argument('L', codeBlock)
    fun L(code: JavaCodeRef) = L(code.codeBlock)
    fun L(block: Block) = L(code(block))

    internal fun build() = JPCodeBlock.of(block(), *arguments.toTypedArray())

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(block: Block) = JavaCodeRef {
            JavaCodeScope(poetesse.config, block).build()
        }
    }
}
