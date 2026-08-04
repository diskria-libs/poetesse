package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.extensions.joinWithTrailing

@PoetesseJava
class JavaVariableScope internal constructor(
    val name: String,
    private val type: JavaCodeRef?,
) : JavaFinalOnlyModifierContainer,
    JavaAnnotationContainer {

    private val modifiers: MutableList<JPModifier> = mutableListOf()
    private val annotations: MutableList<JPAnnotation> = mutableListOf()

    private var initializer: JavaCodeRef? = null

    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { modifiers += it }
    )
    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { annotations += it },
    )

    fun initializer(block: JavaCodeBuilder) {
        initializer = JavaCodeScope.of(block)
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
            val type = type?.let { L(it) } ?: L("var")
            val initializer = initializer?.takeIf { !it.codeBlock.isEmpty }?.let { " = ${L(it)}" }.orEmpty()
            "$annotations$modifiers$type $name$initializer"
        }
    }
}
