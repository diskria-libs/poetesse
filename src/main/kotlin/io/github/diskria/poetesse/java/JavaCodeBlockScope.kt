package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava

@PoetesseJava
class JavaCodeBlockScope : JavaCodeBlockContainerScope.External {

    private val codeBlocks: MutableList<JPCodeBlock> = mutableListOf()

    internal val codeBlockContainerInternalScope = JavaCodeBlockContainerScope.Internal.of(
        append = { codeBlocks += it }
    )

    internal fun build(): List<JPCodeBlock> =
        codeBlocks
}
