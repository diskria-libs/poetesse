package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope

class JavaCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPCodeBlockBuilder = JPCodeBlock.builder(),
) : JavaCodeBlockTrait {

    internal typealias Block = JavaCodeBlockScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainerInternal(builder::addStatement)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = JavaCodeBlockScope(poetesse.config)
    }
}

class JavaEmbeddableCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    internal val statements: MutableList<JPCodeBlock> = mutableListOf()
) : JavaCodeBlockTrait {

    internal typealias Block = JavaEmbeddableCodeBlockScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainerInternal { statements += it }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = JavaEmbeddableCodeBlockScope(poetesse.config)
    }
}
