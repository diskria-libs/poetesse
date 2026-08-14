package io.github.diskria.poetesse.java

interface JavaCodeBlockFactory : JavaCodeFactory {

    fun codeBlock(block: JavaEmbeddableCodeBlockScope.Block): JavaCodeBlockRef =
        JavaCodeBlockRef { JavaEmbeddableCodeBlockScope(settings).apply(block).statements }
}
