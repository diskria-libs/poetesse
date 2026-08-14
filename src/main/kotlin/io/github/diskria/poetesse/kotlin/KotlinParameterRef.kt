package io.github.diskria.poetesse.kotlin

class KotlinParameterRef internal constructor(val name: String, build: () -> KPParameter) {
    internal val spec: KPParameter by lazy(build)
}
