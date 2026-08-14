package io.github.diskria.poetesse.java

class JavaParameterRef internal constructor(val name: String, build: () -> JPParameter) {
    internal val spec: JPParameter by lazy(build)
}
