package io.github.diskria.poetesse.java

class JavaDeferredMethod internal constructor(
    val name: String,
    buildSpec: () -> JPMethod
) {
    internal val spec: JPMethod by lazy(buildSpec)
}
