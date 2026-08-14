package io.github.diskria.poetesse.java

interface JavaCodeBlockFactory : JavaCodeFactory {

    fun codeBlock(block: JavaEmbeddedCodeBlockBuilder): JavaCodeBlockRef =
        JavaCodeBlockRef { JavaEmbeddedCodeBlockScope(settings).apply(block).statements }
}
