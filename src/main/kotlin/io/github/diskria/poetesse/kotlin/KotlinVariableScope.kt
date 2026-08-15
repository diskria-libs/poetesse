package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.joinWithTrailing
import io.github.diskria.poetesse.interop.PoetesseXScope
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

    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { modifiers += it }
    )
    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { annotations += it },
    )

    fun mutable(mutable: Boolean = true) {
        isMutable = mutable
    }

    fun initializer(block: KotlinCodeScope.Block) {
        initializer = KotlinCodeScope.of(block)
    }

    internal fun build(): KotlinCodeScope.Block {
        val annotations = this@KotlinVariableScope.annotations
        val modifiers = this@KotlinVariableScope.modifiers
        val isMutable = this@KotlinVariableScope.isMutable
        val type = this@KotlinVariableScope.type
        val name = this@KotlinVariableScope.name
        val initializer = this@KotlinVariableScope.initializer
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
        context(scope: PoetesseXScope)
        fun of(name: String, type: XTypeName?) = KotlinVariableScope(scope.config, name, type)
    }
}
