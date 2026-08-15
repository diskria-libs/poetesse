package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XClassName

class JavaTypeRef internal constructor(
    val name: String,
    internal val build: (XClassName) -> JPType,
)
