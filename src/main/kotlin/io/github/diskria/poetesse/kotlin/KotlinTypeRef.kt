package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName

class KotlinTypeRef internal constructor(
    val name: String,
    internal val build: (XClassName) -> KPType
)
