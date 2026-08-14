package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.joinWithTrailing
import io.github.diskria.poetesse.interop.XTypeName

class JavaVariableScope internal constructor(
    override val settings: Poetesse.Settings,
    val name: String,
    private val type: XTypeName?,
) : PoetesseJavaScope,
    JavaAnnotationContainer,
    JavaModifierContainer {

    internal typealias Block = JavaVariableScope.() -> Unit

    private val modifiers: MutableList<JPModifier> = mutableListOf()
    private val annotations: MutableList<JPAnnotation> = mutableListOf()

    private var initializer: JavaCodeRef? = null

    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { modifiers += it }
    )
    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { annotations += it },
    )

    fun final() {
        modifiers(JPModifier.FINAL)
    }

    fun initializer(block: JavaCodeBuilder) {
        initializer = JavaCodeScope.of(settings, block)
    }

    internal fun build(): JavaCodeBuilder {
        val annotations = this@JavaVariableScope.annotations
        val modifiers = this@JavaVariableScope.modifiers
        val type = this@JavaVariableScope.type
        val name = this@JavaVariableScope.name
        val initializer = this@JavaVariableScope.initializer
        return {
            val annotations = annotations.joinWithTrailing(" ") { L(it) }
            val modifiers = modifiers.joinWithTrailing(" ")
            val type = type?.let { T(it, resolveNullability = true) } ?: L("var")
            val initializer = initializer?.takeIf { !it.codeBlock.isEmpty }?.let { " = ${L(it)}" }.orEmpty()
            "$annotations$modifiers$type $name$initializer"
        }
    }
}
