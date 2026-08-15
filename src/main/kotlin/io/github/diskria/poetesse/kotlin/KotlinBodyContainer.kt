package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope

sealed interface KotlinBodyContainer : PoetesseKotlinScope

fun KotlinBodyContainer.body(block: KotlinBodyScope.Block = {}) {
    KotlinBodyScope.of(internal::append).apply(block)
}

fun KotlinBodyContainer.expression(block: KotlinCodeScope.Block) {
    body { line { "return ${L(block)}" } }
}

internal interface KotlinBodyContainerInternal {

    fun append(statement: KPCodeBlock)

    companion object {
        fun of(
            append: (statement: KPCodeBlock) -> Unit,
        ): KotlinBodyContainerInternal = object : KotlinBodyContainerInternal {
            override fun append(statement: KPCodeBlock) = append(statement)
        }
    }
}

class KotlinBodyScope private constructor(
    override val config: Poetesse.Config,
    append: (statement: KPCodeBlock) -> Unit,
) : KotlinCodeBlockContainer {

    internal typealias Block = KotlinBodyScope.() -> Unit

    internal val codeBlockContainer = KotlinCodeBlockContainerInternal.of(append)

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(append: (statement: KPCodeBlock) -> Unit) = KotlinBodyScope(scope.config, append)
    }
}

private val KotlinBodyContainer.internal: KotlinBodyContainerInternal
    get() = when (this) {
        is KotlinPropertyGetterScope -> statementContainer
        is KotlinPropertySetterScope -> statementContainer
        is KotlinConstructorScope -> statementContainer
        is KotlinFunctionScope -> statementContainer
    }
