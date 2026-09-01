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
    JavaAnnotationTrait,
    JavaModifierTrait {

    internal typealias Block = JavaVariableScope.() -> Unit

    private val annotations: MutableList<JPAnnotation> = mutableListOf()
    private val modifiers: MutableList<JPModifier> = mutableListOf()

    internal val annotationContainer by lazy { JavaAnnotationContainer { annotations += it } }
    internal val modifierContainer by lazy { JavaModifierContainer { modifiers += it } }

    private var initializer: JavaCodeRef? = null

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
