package io.github.diskria.poetesse.java

class JavaDeferredCode internal constructor(build: () -> JPCodeBlock) {
    internal val codeBlock: JPCodeBlock by lazy(build)
}
