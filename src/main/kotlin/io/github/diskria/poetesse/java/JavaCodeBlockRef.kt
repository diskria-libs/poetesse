package io.github.diskria.poetesse.java

class JavaCodeBlockRef internal constructor(build: () -> List<JPCodeBlock>) {
    internal val statements: List<JPCodeBlock> by lazy(build)
}
