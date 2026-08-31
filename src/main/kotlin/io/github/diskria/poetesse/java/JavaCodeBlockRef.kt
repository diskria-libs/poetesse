package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XCodeBlockMutationType

class JavaCodeBlockRef internal constructor(build: () -> List<JavaCodeBlockMutation>) {
    internal val mutations: List<JavaCodeBlockMutation> by lazy(build)
}

class JavaCodeBlockMutation(val type: XCodeBlockMutationType, val codeBlock: JPCodeBlock)
