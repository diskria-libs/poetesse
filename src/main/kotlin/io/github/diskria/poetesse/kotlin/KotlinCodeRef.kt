package io.github.diskria.poetesse.kotlin

class KotlinCodeRef internal constructor(build: () -> KPCodeBlock) {
    internal val codeBlock: KPCodeBlock by lazy(build)
}
