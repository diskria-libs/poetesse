package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.PoetesseScope

sealed interface KotlinBodyContainer : PoetesseScope

fun KotlinBodyContainer.body(block: KotlinBodyScope.() -> Unit) {
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

@PoetesseKotlin
class KotlinBodyScope(
    override val settings: Poetesse.Settings,
    append: (statement: KPCodeBlock) -> Unit,
) : KotlinCodeBlockContainer {
    internal val codeBlockContainer = KotlinCodeBlockContainerInternal.of(append)
}

private val KotlinBodyContainer.internal: KotlinBodyContainerInternal
    get() = when (this) {
        is KotlinPropertyGetterScope -> bodyContainer
        is KotlinPropertySetterScope -> bodyContainer
        is KotlinConstructorScope -> bodyContainer
        is KotlinFunctionScope -> bodyContainer
    }
