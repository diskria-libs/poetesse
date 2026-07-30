package io.github.diskria.poetesse.java

class JavaDeferredCodeBlock internal constructor(buildBlock: JavaMultiLineCodeBlockScope.() -> Unit) {
    internal val statements: List<JPCodeBlock> by lazy {
        JavaMultiLineCodeBlockScope().apply(buildBlock).buildStatements()
    }
}
