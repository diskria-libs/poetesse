package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement

typealias KotlinCodeBlockBuilder = KotlinCodeBlockScope.() -> Unit

class KotlinCodeBlockScope internal constructor(
    override val settings: Poetesse.Settings,
    private val builder: KPCodeBlockBuilder = KPCodeBlock.builder(),
) : KotlinCodeBlockContainer {

    internal val codeBlockContainer = KotlinCodeBlockContainerInternal.of(
        append = { builder.addStatement(it) }
    )

    internal fun build(): KPCodeBlock =
        builder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, block: KotlinCodeBlockBuilder): KotlinCodeBlockScope =
            KotlinCodeBlockScope(settings).apply(block)
    }
}

typealias KotlinEmbeddableCodeBlockBuilder = KotlinEmbeddableCodeBlockScope.() -> Unit

class KotlinEmbeddableCodeBlockScope internal constructor(
    override val settings: Poetesse.Settings,
    internal val statements: MutableList<KPCodeBlock> = mutableListOf()
) : KotlinCodeBlockContainer {

    internal val codeBlockContainer = KotlinCodeBlockContainerInternal.of(
        append = { statements += it }
    )
}
