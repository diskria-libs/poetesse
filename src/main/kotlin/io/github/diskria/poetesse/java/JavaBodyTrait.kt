package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope

sealed interface JavaBodyTrait : PoetesseJavaScope

fun JavaBodyTrait.body(block: JavaBodyScope.Block = {}) {
    JavaBodyScope.of(internal.append).apply(block)
}

internal class JavaBodyContainer(val append: (statement: JPCodeBlock) -> Unit)

class JavaBodyScope private constructor(
    override val config: Poetesse.Config,
    internal val codeBlockContainer: JavaCodeBlockContainer,
) : JavaCodeBlockTrait {

    internal typealias Block = JavaBodyScope.() -> Unit

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(append: (statement: JPCodeBlock) -> Unit) =
            JavaBodyScope(poetesse.config, JavaCodeBlockContainer(append))
    }
}

private val JavaBodyTrait.internal: JavaBodyContainer
    get() = when (this) {
        is JavaConstructorScope -> statementContainer
        is JavaMethodScope -> statementContainer
    }
