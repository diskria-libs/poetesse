package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse

class JavaCodeBlockScope internal constructor(
    override val settings: Poetesse.Settings,
    private val builder: JPCodeBlockBuilder = JPCodeBlock.builder(),
) : JavaCodeBlockContainer {

    typealias Block = JavaCodeBlockScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
        append = { builder.addStatement(it) }
    )

    internal fun build(): JPCodeBlock =
        builder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, block: Block) = JavaCodeBlockScope(settings).apply(block)
    }
}

class JavaEmbeddableCodeBlockScope internal constructor(
    override val settings: Poetesse.Settings,
    internal val statements: MutableList<JPCodeBlock> = mutableListOf()
) : JavaCodeBlockContainer {

    typealias Block = JavaEmbeddableCodeBlockScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
        append = { statements += it }
    )
}
