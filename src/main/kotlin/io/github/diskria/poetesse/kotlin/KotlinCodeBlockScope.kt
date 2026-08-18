package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement
import io.github.diskria.poetesse.interop.PoetesseScope

class KotlinCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    private val builder: KPCodeBlockBuilder = KPCodeBlock.builder(),
) : PoetesseKotlinScope,
    KotlinCodeBlockTrait {

    internal typealias Block = KotlinCodeBlockScope.() -> Unit

    internal val codeBlockContainer = KotlinCodeBlockContainer(builder::addStatement)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = KotlinCodeBlockScope(poetesse.config)
    }
}

class KotlinEmbeddableCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    internal val statements: MutableList<KPCodeBlock> = mutableListOf()
) : KotlinCodeBlockTrait {

    internal typealias Block = KotlinEmbeddableCodeBlockScope.() -> Unit

    internal val codeBlockContainer = KotlinCodeBlockContainer { statements += it }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = KotlinEmbeddableCodeBlockScope(poetesse.config)
    }
}
