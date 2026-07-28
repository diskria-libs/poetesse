package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.XClassName

class JavaDeferredType(internal val name: String, internal val build: (XClassName) -> JPType)
