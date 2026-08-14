package io.github.diskria.poetesse.kotlin

interface KotlinCodeBlockFactory : KotlinCodeFactory {

    fun codeBlock(build: KotlinEmbeddableCodeBlockBuilder): KotlinCodeBlockRef =
        KotlinCodeBlockRef { KotlinEmbeddableCodeBlockScope(settings).apply(build).statements }
}
