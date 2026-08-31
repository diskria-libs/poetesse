package io.github.diskria.poetesse.kotlin

interface KotlinCodeBlockFactory : KotlinCodeFactory

fun KotlinCodeBlockFactory.codeBlock(block: KotlinCodeBlockContainerScope.Block) =
    KotlinCodeBlockRef { KotlinCodeBlockContainerScope.of().apply(block).build() }
