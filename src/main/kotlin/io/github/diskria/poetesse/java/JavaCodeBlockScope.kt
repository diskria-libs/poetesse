package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope

class JavaCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPCodeBlockBuilder = JPCodeBlock.builder(),
) : JavaCodeBlockContainer {

    typealias Block = JavaCodeBlockScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
        append = { builder.addStatement(it) }
    )

    internal fun build() = builder.build()

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(block: Block) = JavaCodeBlockScope(scope.config).apply(block)
    }
}

class JavaEmbeddableCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    internal val statements: MutableList<JPCodeBlock> = mutableListOf()
) : JavaCodeBlockContainer {

    typealias Block = JavaEmbeddableCodeBlockScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
        append = { statements += it }
    )

    internal companion object {
        context(scope: PoetesseXScope)
        fun of() = JavaEmbeddableCodeBlockScope(scope.config)
    }
}
