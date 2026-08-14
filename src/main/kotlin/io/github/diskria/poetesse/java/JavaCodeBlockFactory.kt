package io.github.diskria.poetesse.java

interface JavaCodeBlockFactory : JavaCodeFactory {

    fun codeBlock(block: JavaEmbeddableCodeBlockBuilder): JavaCodeBlockRef =
        JavaCodeBlockRef { JavaEmbeddableCodeBlockScope(settings).apply(block).statements }
}
