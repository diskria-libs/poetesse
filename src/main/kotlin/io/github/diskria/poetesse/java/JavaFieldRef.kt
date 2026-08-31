package io.github.diskria.poetesse.java

class JavaFieldRef internal constructor(val name: String, build: () -> JPField) {
    internal val spec: JPField by lazy(build)

    override fun toString(): String = name
}
