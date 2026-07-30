package io.github.diskria.poetesse.java

class JavaCodeRef internal constructor(build: () -> JPCodeBlock) {

    internal val codeBlock: JPCodeBlock by lazy(build)
}
