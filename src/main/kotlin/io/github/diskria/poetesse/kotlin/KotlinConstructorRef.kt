package io.github.diskria.poetesse.kotlin

class KotlinConstructorRef internal constructor(internal val isPrimary: Boolean, build: () -> KPFunction) {

    internal val spec: KPFunction by lazy(build)
}
