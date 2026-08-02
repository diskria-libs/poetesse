package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava

typealias JavaVariableBuilder = JavaVariableScope.() -> String

@PoetesseJava
class JavaVariableScope private constructor(
    val name: String,
    val type: JavaCodeBuilder,
    private val value: JavaVariableBuilder
) : JavaCodeArgumentsScope(),
    JavaModifierContainer {

    private val modifiers: MutableList<String> = mutableListOf()

    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { modifiers -> this@JavaVariableScope.modifiers += modifiers.map { it.toString() } }
    )

    private fun build(): JPCodeBlock = build {
        val type = L(type)
        val value = value()
        val modifiers = modifiers.distinct().joinToString(separator = "") { "$it " }
        "$modifiers$type $name = $value"
    }

    internal companion object {
        fun of(name: String, type: JavaCodeBuilder, value: JavaVariableBuilder): JavaCodeRef =
            JavaCodeRef { JavaVariableScope(name, type, value).build() }
    }
}
