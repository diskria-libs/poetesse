package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava

typealias JavaCodeBlockBuilder = JavaCodeBlockScope.() -> Unit

@PoetesseJava
class JavaCodeBlockScope internal constructor(
    override val settings: Poetesse.Settings,
    private val builder: JPCodeBlockBuilder = JPCodeBlock.builder(),
) : JavaCodeBlockContainer {

    internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
        append = { builder.addStatement(it) }
    )

    internal fun build(): JPCodeBlock =
        builder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, block: JavaCodeBlockBuilder): JavaCodeBlockScope =
            JavaCodeBlockScope(settings).apply(block)
    }
}

typealias JavaEmbeddedCodeBlockBuilder = JavaEmbeddedCodeBlockScope.() -> Unit

@PoetesseJava
class JavaEmbeddedCodeBlockScope internal constructor(
    override val settings: Poetesse.Settings,
    internal val statements: MutableList<JPCodeBlock> = mutableListOf()
) : JavaCodeBlockContainer {

    internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
        append = { statements += it }
    )
}
