package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.extensions.joinWithTrailing
import io.github.diskria.poetesse.interop.XTypeName

@PoetesseKotlin
class KotlinVariableScope internal constructor(
    val name: String,
    private val type: XTypeName?,
) : KotlinAnnotationContainer,
    KotlinModifierContainer {

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

    fun initializer(block: KotlinCodeBuilder) {
        initializer = KotlinCodeScope.of(block)
    }

    internal fun build(): KotlinCodeBuilder {
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
}
