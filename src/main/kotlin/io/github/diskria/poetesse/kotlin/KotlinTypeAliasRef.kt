package io.github.diskria.poetesse.kotlin

class KotlinTypeAliasRef internal constructor(val name: String, build: () -> KPTypeAlias) {
    internal val spec: KPTypeAlias by lazy(build)
}
