package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XCodeBlockMutationType

class KotlinCodeBlockRef internal constructor(build: () -> List<KotlinCodeBlockMutation>) {
    internal val mutations: List<KotlinCodeBlockMutation> by lazy(build)
}

class KotlinCodeBlockMutation(val type: XCodeBlockMutationType, val codeBlock: KPCodeBlock)
