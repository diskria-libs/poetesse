package io.github.diskria.poetesse.kotlin

class KotlinConstructorRef internal constructor(
    internal val isPrimary: Boolean,
    internal val build: (KPTypeBuilder) -> KPFunction,
)
