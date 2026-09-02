package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.toCodeString
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

class KotlinCodeScope private constructor(
    override val config: Poetesse.Config,
    private val block: Block,
    private val arguments: MutableList<Any?> = mutableListOf(),
) : PoetesseKotlinScope,
    KotlinCodeFactory {

    internal typealias Block = KotlinCodeScope.() -> String

    fun argument(mask: Char, argument: Any?): String {
        arguments += argument
        return "%$mask"
    }

    fun T(value: XTypeName) = argument('T', value.interopToKotlin())
    fun T(value: KClass<*>, nullable: Boolean = false) = T(xType(value, nullable = nullable))
    inline fun <reified T> T(nullable: Boolean = true) = T(T::class, nullable)
    inline fun <reified T : Any> T() = T<T>(nullable = false)

    fun S(value: String) = argument('S', value)

    fun L(value: Boolean) = argument('L', value)
    fun L(value: Byte) = L(value.toCodeString())
    fun L(value: Short) = argument('L', value)
    fun L(value: Int) = argument('L', value)
    fun L(value: Long) = L("${value}L")
    fun L(value: Char) = L(value.toCodeString())
    fun L(value: Float) = L("${value}f")
    fun L(value: Double) = argument('L', value)
    fun L(value: String) = argument('L', value)
    fun L(value: KPAnnotation) = argument('L', value)
    fun L(value: KotlinAnnotationRef) = L(value.spec)

    fun L(codeBlock: KPCodeBlock) = argument('L', codeBlock)
    fun L(code: KotlinCodeRef) = L(code.codeBlock)
    fun L(block: Block) = L(code(block))

    internal fun build() = KPCodeBlock.of(block(), *arguments.toTypedArray())

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(block: Block) = KotlinCodeRef {
            KotlinCodeScope(poetesse.config, block).build()
        }
    }
}
