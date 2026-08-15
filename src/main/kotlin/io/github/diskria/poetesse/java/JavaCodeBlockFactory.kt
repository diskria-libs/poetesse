package io.github.diskria.poetesse.java

interface JavaCodeBlockFactory : JavaCodeFactory

fun JavaCodeBlockFactory.codeBlock(block: JavaEmbeddableCodeBlockScope.Block) =
    JavaCodeBlockRef { JavaEmbeddableCodeBlockScope.of().apply(block).statements }
