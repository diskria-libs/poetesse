package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope

sealed interface JavaBodyContainer : PoetesseJavaScope

fun JavaBodyContainer.body(block: JavaBodyScope.Block = {}) {
    JavaBodyScope.of(internal::append).apply(block)
}

internal interface JavaBodyContainerInternal {

    fun append(statement: JPCodeBlock)

    companion object {
        fun of(
            append: (statement: JPCodeBlock) -> Unit,
        ): JavaBodyContainerInternal = object : JavaBodyContainerInternal {
            override fun append(statement: JPCodeBlock) = append(statement)
        }
    }
}

class JavaBodyScope private constructor(
    override val config: Poetesse.Config,
    append: (statement: JPCodeBlock) -> Unit,
) : JavaCodeBlockContainer {

    internal typealias Block = JavaBodyScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(append)

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
