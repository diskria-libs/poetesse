package io.github.diskria.poetesse.java

class JavaDeferredCodeBlock internal constructor(build: JavaCodeBlockScope.() -> Unit) {
    internal val codeBlocks: List<JPCodeBlock> by lazy {
        JavaCodeBlockScope().apply(build).build()
    }
}
