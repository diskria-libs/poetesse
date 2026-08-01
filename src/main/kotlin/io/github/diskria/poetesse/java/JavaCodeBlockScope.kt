package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava

@PoetesseJava
class JavaCodeBlockScope internal constructor() : JavaCodeBlockContainer {

    private val codeBlocks: MutableList<JPCodeBlock> = mutableListOf()

    internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
        append = { codeBlocks += it }
    )

    internal fun build(): List<JPCodeBlock> =
        codeBlocks
}
