package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope

sealed interface KotlinBodyTrait : PoetesseKotlinScope

fun KotlinBodyTrait.body(block: KotlinBodyScope.Block = {}) {
    KotlinBodyScope.of(internal.append).apply(block)
}

fun KotlinBodyTrait.expression(block: KotlinCodeScope.Block) {
    body { line { "return ${L(block)}" } }
}

internal class KotlinBodyContainerInternal(val append: (statement: KPCodeBlock) -> Unit)

class KotlinBodyScope private constructor(
    override val config: Poetesse.Config,
    internal val codeBlockContainer: KotlinCodeBlockContainerInternal,
) : KotlinCodeBlockTrait {

    internal typealias Block = KotlinBodyScope.() -> Unit

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(append: (statement: KPCodeBlock) -> Unit) =
            KotlinBodyScope(poetesse.config, KotlinCodeBlockContainerInternal(append))
    }
}

private val KotlinBodyTrait.internal: KotlinBodyContainerInternal
    get() = when (this) {
        is KotlinPropertyGetterScope -> statementContainer
        is KotlinPropertySetterScope -> statementContainer
        is KotlinConstructorScope -> statementContainer
        is KotlinFunctionScope -> statementContainer
    }
