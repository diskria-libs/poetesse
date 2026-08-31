package io.github.diskria.poetesse.kotlin

class KotlinPropertyRef internal constructor(val name: String, build: () -> KPProperty) {
    internal val spec: KPProperty by lazy(build)

    override fun toString(): String = name
}
