package io.github.diskria.poetesse.java

interface JavaCodeBlockFactory

fun JavaCodeBlockFactory.codeBlock(block: JavaMultiLineCodeBlockScope.() -> Unit): JavaDeferredCodeBlock =
    JavaMultiLineCodeBlockScope().apply(block).build()
