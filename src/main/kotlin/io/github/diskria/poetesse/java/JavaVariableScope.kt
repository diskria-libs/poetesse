package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.extensions.joinWithTrailing

typealias JavaVariableBuilder = JavaVariableScope.() -> String

@PoetesseJava
class JavaVariableScope private constructor(
    val name: String,
    val type: JavaCodeBuilder,
    private val value: JavaVariableBuilder
) : JavaCodeArgumentsScope(),
    JavaModifierContainer,
    JavaAnnotationContainer {

    private val modifiers: MutableList<String> = mutableListOf()
    private val annotations: MutableList<JPAnnotation> = mutableListOf()

    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { modifiers += it.map { modifier -> modifier.toString() } }
    )
    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { annotations += it },
    )

    private fun build(): JPCodeBlock {
        val value = value()
        val (annotations, type) = withPrepend {
            annotations.map { L(it) }.joinWithTrailing(" ") to L(type)
        }
        val modifiers = modifiers.joinWithTrailing(" ")
        return build { "$annotations$modifiers$type $name = $value" }
    }

    internal companion object {
        fun of(name: String, type: JavaCodeBuilder, value: JavaVariableBuilder): JavaCodeRef =
            JavaCodeRef { JavaVariableScope(name, type, value).build() }
    }
}
