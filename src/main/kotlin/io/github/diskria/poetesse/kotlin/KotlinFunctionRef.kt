package io.github.diskria.poetesse.kotlin

class KotlinFunctionRef internal constructor(val name: String, build: () -> KPFunction) {

    internal val spec: KPFunction by lazy(build)
}
