package io.github.diskria.poetesse.java

interface JavaCodeBlockFactory : JavaCodeFactory

fun JavaCodeBlockFactory.codeBlock(block: JavaCodeBlockContainerScope.Block) =
    JavaCodeBlockRef { JavaCodeBlockContainerScope.of().apply(block).build() }
