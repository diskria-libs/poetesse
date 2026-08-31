package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.applyCodeBlockMutation
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XCodeBlockMutationType

class JavaCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    internal val isComment: Boolean,
    private val builder: JPCodeBlockBuilder = JPCodeBlock.builder(),
) : PoetesseJavaScope,
    JavaCodeBlockTrait {

    internal typealias Block = JavaCodeBlockScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainer(builder::applyCodeBlockMutation)

//        if (isComment) {
//            builder.add("$[")
//            builder.add(it)
//            builder.add("\n$]")
//        }

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(isComment: Boolean = false) = JavaCodeBlockScope(poetesse.config, isComment)
    }
}

class JavaCodeBlockContainerScope private constructor(
    override val config: Poetesse.Config,
    internal val mutations: MutableList<JavaCodeBlockMutation> = mutableListOf()
) : PoetesseJavaScope,
    JavaCodeBlockTrait {

    internal typealias Block = JavaCodeBlockContainerScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainer { command, codeBlock ->
        mutations += JavaCodeBlockMutation(command, codeBlock)
    }

    internal fun build() = mutations

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = JavaCodeBlockContainerScope(poetesse.config)
    }
}

class JavaControlFlowScope private constructor(
    override val config: Poetesse.Config,
    private val mutations: MutableList<JavaCodeBlockMutation> = mutableListOf(),
) : PoetesseJavaScope,
    JavaCodeBlockTrait {

    internal typealias Block = JavaControlFlowScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainer { command, codeBlock ->
        mutations += JavaCodeBlockMutation(command, codeBlock)
    }

    private var hasStarted: Boolean = false
    private var endingCodeBlock: JPCodeBlock = JPCodeBlock.of("")

    fun branch(header: JavaCodeScope.Block, block: Block = {}) {
        val mutationType = if (!hasStarted) {
            hasStarted = true
            XCodeBlockMutationType.BEGIN_CONTROL_FLOW
        } else {
            XCodeBlockMutationType.NEXT_CONTROL_FLOW
        }
        codeBlockContainer.applyCodeBlockMutation(mutationType, JavaCodeScope.of(header).codeBlock)
        apply(block)
    }

    fun branch(header: String, block: Block = {}) {
        branch({ header }, block)
    }

    fun ending(code: String) {
        endingCodeBlock = JPCodeBlock.of(code)
    }

    fun ending(block: JavaCodeScope.Block) {
        endingCodeBlock = JavaCodeScope.of(block).codeBlock
    }

    internal fun build(): List<JavaCodeBlockMutation> {
        if (hasStarted) {
            codeBlockContainer.applyCodeBlockMutation(XCodeBlockMutationType.END_CONTROL_FLOW, endingCodeBlock)
        }
        return mutations
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = JavaControlFlowScope(poetesse.config)
    }
}
