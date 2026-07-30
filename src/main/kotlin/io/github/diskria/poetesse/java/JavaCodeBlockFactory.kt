package io.github.diskria.poetesse.java

interface JavaCodeBlockFactory {
    fun codeBlock(block: JavaMultiLineCodeBlockScope.() -> Unit): JavaDeferredCodeBlock =
        JavaDeferredCodeBlock(block)
}
