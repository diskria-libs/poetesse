package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XCodeBlockMutationType

sealed interface JavaBodyTrait : PoetesseJavaScope

fun JavaBodyTrait.body(block: JavaBodyScope.Block = {}) {
    JavaBodyScope.of(container).apply(block)
}

internal class JavaBodyContainer(
    val applyCodeBlockMutation: (type: XCodeBlockMutationType, codeBlock: JPCodeBlock) -> Unit
)

class JavaBodyScope private constructor(
    override val config: Poetesse.Config,
    internal val codeBlockContainer: JavaCodeBlockContainer,
) : JavaCodeBlockTrait {

    internal typealias Block = JavaBodyScope.() -> Unit

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(container: JavaBodyContainer) =
            JavaBodyScope(poetesse.config, JavaCodeBlockContainer(container.applyCodeBlockMutation))
    }
}

private val JavaBodyTrait.container: JavaBodyContainer
    get() = when (this) {
        is JavaConstructorScope -> statementContainer
        is JavaMethodScope -> statementContainer
    }
