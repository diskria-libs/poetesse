package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement
import io.github.diskria.poetesse.interop.PoetesseXScope

class KotlinCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    private val builder: KPCodeBlockBuilder = KPCodeBlock.builder(),
) : KotlinCodeBlockContainer {

    internal typealias Block = KotlinCodeBlockScope.() -> Unit

    internal val codeBlockContainer = KotlinCodeBlockContainerInternal { builder.addStatement(it) }

    internal fun build() = builder.build()

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(block: Block) = KotlinCodeBlockScope(scope.config).apply(block)
    }
}

class KotlinEmbeddableCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    internal val statements: MutableList<KPCodeBlock> = mutableListOf()
) : KotlinCodeBlockContainer {

    internal typealias Block = KotlinEmbeddableCodeBlockScope.() -> Unit

    internal val codeBlockContainer = KotlinCodeBlockContainerInternal { statements += it }

    internal companion object {
        context(scope: PoetesseXScope)
        fun of() = KotlinEmbeddableCodeBlockScope(scope.config)
    }
}
