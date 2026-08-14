package io.github.diskria.poetesse.kotlin

interface KotlinCodeBlockFactory : KotlinCodeFactory {

    fun codeBlock(build: KotlinEmbeddableCodeBlockScope.Block): KotlinCodeBlockRef =
        KotlinCodeBlockRef { KotlinEmbeddableCodeBlockScope(settings).apply(build).statements }
}
