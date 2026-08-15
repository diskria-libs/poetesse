package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.joinWithTrailing
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName

class KotlinVariableScope private constructor(
    override val config: Poetesse.Config,
    val name: String,
    private val type: XTypeName?,
) : PoetesseKotlinScope,
    KotlinAnnotationContainer,
    KotlinModifierContainer {

    internal typealias Block = KotlinVariableScope.() -> Unit

    private var isMutable: Boolean = false
    private val modifiers: MutableList<KPModifier> = mutableListOf()
    private val annotations: MutableList<KPAnnotation> = mutableListOf()

    private var initializer: KotlinCodeRef? = null

    internal val modifierContainer = KotlinModifierContainerInternal { modifiers += it }
    internal val annotationContainer = KotlinAnnotationContainerInternal { annotations += it }

    fun mutable(mutable: Boolean = true) {
        isMutable = mutable
    }

    fun initializer(block: KotlinCodeScope.Block) {
        initializer = KotlinCodeScope.of(block)
    }

    internal fun build(): KotlinCodeScope.Block {
        val annotations = annotations
        val modifiers = modifiers
        val isMutable = isMutable
        val type = type
        val name = name
        val initializer = initializer
        return {
            val annotations = annotations.joinWithTrailing(" ") { L(it) }
            val modifiers = modifiers.joinWithTrailing(" ")
            val keyword = if (isMutable) "var" else "val"
            val type = type?.let { ": ${T(it)}" }.orEmpty()
            val initializer = initializer?.takeIf { it.codeBlock.isNotEmpty() }?.let { " = ${L(it)}" }.orEmpty()
            "$annotations$modifiers$keyword $name$type$initializer"
        }
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, type: XTypeName?) = KotlinVariableScope(poetesse.config, name, type)
    }
}
