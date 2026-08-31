package io.github.diskria.poetesse.kotlin

interface KotlinCodeBlockFactory : KotlinCodeFactory

fun KotlinCodeBlockFactory.codeBlock(block: KotlinEmbeddableCodeBlockScope.Block) =
    KotlinCodeBlockRef { KotlinEmbeddableCodeBlockScope.of().apply(block).build() }
