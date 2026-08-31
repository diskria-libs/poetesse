package io.github.diskria.poetesse.kotlin

class KotlinContextParameterRef internal constructor(val name: String, build: () -> KPContextParameter) {
    internal val spec: KPContextParameter by lazy(build)

    override fun toString(): String = name
}
