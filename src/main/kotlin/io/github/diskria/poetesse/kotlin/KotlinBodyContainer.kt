package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse

sealed interface KotlinBodyContainer : PoetesseKotlinScope

fun KotlinBodyContainer.body(block: KotlinBodyScope.Block = {}) {
    KotlinBodyScope(settings, internal::append).apply(block)
}

fun KotlinBodyContainer.expression(block: KotlinCodeBuilder) {
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

class KotlinBodyScope(
    override val settings: Poetesse.Settings,
    append: (statement: KPCodeBlock) -> Unit,
) : KotlinCodeBlockContainer {

    internal typealias Block = KotlinBodyScope.() -> Unit

    internal val codeBlockContainer = KotlinCodeBlockContainerInternal.of(append)
}

private val KotlinBodyContainer.internal: KotlinBodyContainerInternal
    get() = when (this) {
        is KotlinPropertyGetterScope -> statementContainer
        is KotlinPropertySetterScope -> statementContainer
        is KotlinConstructorScope -> statementContainer
        is KotlinFunctionScope -> statementContainer
    }
