package io.github.diskria.poetesse.kotlin

class KotlinCodeBlockRef internal constructor(build: () -> List<KPCodeBlock>) {
    internal val statements: List<KPCodeBlock> by lazy(build)
}
