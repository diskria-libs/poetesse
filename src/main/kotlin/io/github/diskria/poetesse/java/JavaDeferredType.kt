package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.XClassName

class JavaDeferredType internal constructor(
    val name: String,
    internal val build: (XClassName) -> JPType
)
