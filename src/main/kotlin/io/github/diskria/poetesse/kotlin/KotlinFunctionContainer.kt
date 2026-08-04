package io.github.diskria.poetesse.kotlin

sealed interface KotlinFunctionContainer {

    fun fun_(name: String, block: KotlinFunctionScope.() -> Unit = {}): String {
        internal.holderBuilder.addFunction(KotlinFunctionScope.of(name).apply(block).build())
        return name
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
