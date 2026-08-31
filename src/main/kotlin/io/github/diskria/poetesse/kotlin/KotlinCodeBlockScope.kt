package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.applyCodeBlockMutation
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XCodeBlockMutationType

class KotlinCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    private val builder: KPCodeBlockBuilder = KPCodeBlock.builder(),
) : PoetesseKotlinScope,
    KotlinCodeBlockTrait {

    internal typealias Block = KotlinCodeBlockScope.() -> Unit

    internal val codeBlockContainer = KotlinCodeBlockContainer(builder::applyCodeBlockMutation)

    internal fun build(): KPCodeBlock = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = KotlinCodeBlockScope(poetesse.config)
    }
}

class KotlinCodeBlockContainerScope private constructor(
    override val config: Poetesse.Config,
    private val mutations: MutableList<KotlinCodeBlockMutation> = mutableListOf(),
) : PoetesseKotlinScope,
    KotlinCodeBlockTrait {

    internal typealias Block = KotlinCodeBlockContainerScope.() -> Unit

    internal val codeBlockContainer = KotlinCodeBlockContainer { command, codeBlock ->
        mutations += KotlinCodeBlockMutation(command, codeBlock)
    }

    internal fun build() = mutations

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = KotlinCodeBlockContainerScope(poetesse.config)
    }
}

class KotlinControlFlowScope private constructor(
    override val config: Poetesse.Config,
    private val mutations: MutableList<KotlinCodeBlockMutation> = mutableListOf(),
) : PoetesseKotlinScope,
    KotlinCodeBlockTrait {

    internal typealias Block = KotlinControlFlowScope.() -> Unit

    internal val codeBlockContainer = KotlinCodeBlockContainer { command, codeBlock ->
        mutations += KotlinCodeBlockMutation(command, codeBlock)
    }

    private var hasStarted: Boolean = false
    private var endingCodeBlock: KPCodeBlock = KPCodeBlock.of("")

    fun branch(header: KotlinCodeScope.Block, block: Block = {}) {
        val mutationType = if (!hasStarted) {
            hasStarted = true
            XCodeBlockMutationType.BEGIN_CONTROL_FLOW
        } else {
            XCodeBlockMutationType.NEXT_CONTROL_FLOW
        }
        codeBlockContainer.applyCodeBlockMutation(mutationType, KotlinCodeScope.of(header).codeBlock)
        apply(block)
    }

    fun branch(header: String, block: Block = {}) {
        branch({ header }, block)
    }

    fun ending(code: String) {
        endingCodeBlock = KPCodeBlock.of(code)
    }

    fun ending(block: KotlinCodeScope.Block) {
        endingCodeBlock = KotlinCodeScope.of(block).codeBlock
    }

    internal fun build(): List<KotlinCodeBlockMutation> {
        if (hasStarted) {
            codeBlockContainer.applyCodeBlockMutation(XCodeBlockMutationType.END_CONTROL_FLOW, endingCodeBlock)
        }
        return mutations
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = KotlinControlFlowScope(poetesse.config)
    }
}
