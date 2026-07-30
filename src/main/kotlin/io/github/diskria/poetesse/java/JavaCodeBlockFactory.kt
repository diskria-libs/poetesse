package io.github.diskria.poetesse.java

interface JavaCodeBlockFactory {
    fun codeBlock(block: JavaCodeBlockScope.() -> Unit): JavaDeferredCodeBlock =
        JavaDeferredCodeBlock(block)
}
