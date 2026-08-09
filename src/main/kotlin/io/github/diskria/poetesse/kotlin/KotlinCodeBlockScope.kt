package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseKotlin

@PoetesseKotlin
class KotlinCodeBlockScope internal constructor() : KotlinCodeBlockContainer {

    private val codeBlocks: MutableList<KPCodeBlock> = mutableListOf()

    internal val codeBlockContainer = KotlinCodeBlockContainerInternal.of(
        append = { codeBlocks += it }
    )

    internal fun build(): List<KPCodeBlock> =
        codeBlocks
}
