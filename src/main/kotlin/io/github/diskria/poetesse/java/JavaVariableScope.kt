package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.joinWithTrailing
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName

class JavaVariableScope private constructor(
    override val config: Poetesse.Config,
    val name: String,
    private val type: XTypeName?,
) : PoetesseJavaScope,
    JavaAnnotationContainer,
    JavaModifierContainer {

    internal typealias Block = JavaVariableScope.() -> Unit

    private val modifiers: MutableList<JPModifier> = mutableListOf()
    private val annotations: MutableList<JPAnnotation> = mutableListOf()

    private var initializer: JavaCodeRef? = null

    internal val modifierContainer = JavaModifierContainerInternal { modifiers += it }
    internal val annotationContainer = JavaAnnotationContainerInternal { annotations += it }

    fun final() = modifier(JPModifier.FINAL)

    fun initializer(block: JavaCodeScope.Block) {
        initializer = JavaCodeScope.of(block)
    }

    internal fun build(): JavaCodeScope.Block {
        val annotations = annotations
        val modifiers = modifiers
        val type = type
        val name = name
        val initializer = initializer
        return {
            val annotations = annotations.joinWithTrailing(" ") { L(it) }
            val modifiers = modifiers.joinWithTrailing(" ")
            val type = type?.let { T(it, resolveNullability = true) } ?: L("var")
            val initializer = initializer?.takeIf { !it.codeBlock.isEmpty }?.let { " = ${L(it)}" }.orEmpty()
            "$modifiers$annotations$type $name$initializer"
        }
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, type: XTypeName?) = JavaVariableScope(poetesse.config, name, type)
    }
}
