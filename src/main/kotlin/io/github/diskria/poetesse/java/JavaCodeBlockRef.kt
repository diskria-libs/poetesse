package io.github.diskria.poetesse.java

class JavaCodeBlockRef internal constructor(build: () -> List<JPCodeBlock>) {

    internal val codeBlocks: List<JPCodeBlock> by lazy(build)
}
