package io.github.diskria.poetesse.java

class JavaMethodRef internal constructor(val name: String, build: () -> JPMethod) {
    internal val spec: JPMethod by lazy(build)

    override fun toString(): String = name
}
