package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XFunctionName

sealed interface KotlinFunctionContainer {

    fun fun_(name: String, block: KotlinFunctionScope.() -> Unit = {}): XFunctionName {
        internal.holderBuilder.addFunction(KotlinFunctionScope.of(name).apply(block).build())
        return XFunctionName.of(name)
    }
}

internal interface KotlinFunctionContainerInternal {

    val holderBuilder: KPMemberHolderBuilder

    companion object {
        fun of(
            holderBuilder: KPMemberHolderBuilder,
        ): KotlinFunctionContainerInternal = object : KotlinFunctionContainerInternal {
            override val holderBuilder: KPMemberHolderBuilder = holderBuilder
        }
    }
}

private val KotlinFunctionContainer.internal: KotlinFunctionContainerInternal
    get() = when (this) {
        is KotlinFileScope -> functionContainer
        is KotlinTypeScope -> functionContainer
    }
