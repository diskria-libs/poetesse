package io.github.diskria.poetesse.kotlin

interface KotlinCodeBlockFactory : KotlinCodeFactory {

    fun codeBlock(build: KotlinEmbeddedCodeBlockBuilder): KotlinCodeBlockRef =
        KotlinCodeBlockRef { KotlinEmbeddedCodeBlockScope(settings).apply(build).statements }
}
