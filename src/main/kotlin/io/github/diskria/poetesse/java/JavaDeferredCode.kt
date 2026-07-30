package io.github.diskria.poetesse.java

class JavaDeferredCode internal constructor(buildCode: () -> JPCodeBlock) {
    internal val statement: JPCodeBlock by lazy(buildCode)
}
