package io.github.diskria.poetesse.java

class JavaCodeBlockRef internal constructor(build: JavaCodeBlockScope.() -> Unit) {

    internal val codeBlocks: List<JPCodeBlock> by lazy {
        JavaCodeBlockScope().apply(build).build()
    }
}
