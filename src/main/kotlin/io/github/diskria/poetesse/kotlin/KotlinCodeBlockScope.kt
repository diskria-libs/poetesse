package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.appendCommand
import io.github.diskria.poetesse.interop.PoetesseScope

class KotlinCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    internal val codeBlockContainer: KotlinCodeBlockContainer,
    private val builder: KPCodeBlockBuilder? = null,
) : PoetesseKotlinScope,
    KotlinCodeBlockTrait {

    internal typealias Block = KotlinCodeBlockScope.() -> Unit

    internal fun build(): KPCodeBlock =
        builder?.build() ?: error("Cannot call build() on a Scope created from external container")

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(): KotlinCodeBlockScope {
            val builder = KPCodeBlock.builder()
            return KotlinCodeBlockScope(
                config = poetesse.config,
                codeBlockContainer = KotlinCodeBlockContainer(builder::appendCommand),
                builder = builder
            )
        }

        context(poetesse: PoetesseScope)
        fun of(container: KotlinCodeBlockContainer): KotlinCodeBlockScope {
            return KotlinCodeBlockScope(
                config = poetesse.config,
                codeBlockContainer = container,
                builder = null
            )
        }
    }
}

class KotlinEmbeddableCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    private val commands: MutableList<KotlinCodeBlockCommand> = mutableListOf(),
) : PoetesseKotlinScope,
    KotlinCodeBlockTrait {

    internal typealias Block = KotlinEmbeddableCodeBlockScope.() -> Unit

    internal val codeBlockContainer = KotlinCodeBlockContainer { command, codeBlock ->
        commands += KotlinCodeBlockCommand(command, codeBlock)
    }

    internal fun build() = commands

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = KotlinEmbeddableCodeBlockScope(poetesse.config)
    }
}

class KotlinControlFlowScope private constructor(
    override val config: Poetesse.Config,
    private val codeBlockScope: KotlinEmbeddableCodeBlockScope,
) : PoetesseKotlinScope {

    internal typealias Block = KotlinControlFlowScope.() -> Unit

    private var hasStarted = false
    private var endingCodeBlock: KPCodeBlock = KPCodeBlock.of("")

    fun ending(code: String) {
        endingCodeBlock = KPCodeBlock.of(code)
    }

    fun ending(block: KotlinCodeScope.Block) {
        endingCodeBlock = KotlinCodeScope.of(block).codeBlock
    }

    fun branch(code: String): KotlinBranchScope =
        KotlinBranchScope(KPCodeBlock.of(code))

    fun branch(block: KotlinCodeScope.Block): KotlinBranchScope =
        KotlinBranchScope(KotlinCodeScope.of(block).codeBlock)

    inner class KotlinBranchScope internal constructor(
        private val headerCodeBlock: KPCodeBlock,
    ) {
        fun body(body: KotlinCodeBlockScope.Block) {
            if (!hasStarted) {
                codeBlockScope.codeBlockContainer.append(
                    KotlinCodeBlockCommandType.BEGIN_CONTROL_FLOW,
                    headerCodeBlock
                )
                hasStarted = true
            } else {
                codeBlockScope.codeBlockContainer.append(
                    KotlinCodeBlockCommandType.NEXT_CONTROL_FLOW,
                    headerCodeBlock
                )
            }
            KotlinCodeBlockScope.of(codeBlockScope.codeBlockContainer).apply(body)
        }
    }

    internal fun build(): List<KotlinCodeBlockCommand> {
        if (hasStarted) {
            codeBlockScope.codeBlockContainer.append(
                KotlinCodeBlockCommandType.END_CONTROL_FLOW,
                endingCodeBlock
            )
        }
        return codeBlockScope.build()
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(): KotlinControlFlowScope =
            KotlinControlFlowScope(
                config = poetesse.config,
                codeBlockScope = KotlinEmbeddableCodeBlockScope.of()
            )
    }
}
