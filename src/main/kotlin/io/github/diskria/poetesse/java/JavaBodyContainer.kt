package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope

sealed interface JavaBodyContainer : PoetesseJavaScope

fun JavaBodyContainer.body(block: JavaBodyScope.Block = {}) {
    JavaBodyScope.of(internal.append).apply(block)
}

internal class JavaBodyContainerInternal(val append: (statement: JPCodeBlock) -> Unit)

class JavaBodyScope private constructor(
    override val config: Poetesse.Config,
    append: (statement: JPCodeBlock) -> Unit,
) : JavaCodeBlockContainer {

    internal typealias Block = JavaBodyScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainerInternal(append)

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(append: (statement: JPCodeBlock) -> Unit) = JavaBodyScope(scope.config, append)
    }
}

private val JavaBodyContainer.internal: JavaBodyContainerInternal
    get() = when (this) {
        is JavaConstructorScope -> statementContainer
        is JavaMethodScope -> statementContainer
    }
