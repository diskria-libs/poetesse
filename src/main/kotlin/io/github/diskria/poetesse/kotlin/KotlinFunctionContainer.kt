package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate

sealed interface KotlinFunctionContainer : KotlinFunctionFactory {

    operator fun KotlinFunctionRef.unaryPlus(): String {
        internal.append(spec)
        return name
    }

    fun function(name: String, block: KotlinFunctionScope.Block = {}) =
        +factory.function(name, block)

    fun function(block: KotlinFunctionScope.Block = {}) =
        EagerDelegate { name -> function(name, block) }
}

internal interface KotlinFunctionContainerInternal {

    fun append(function: KPFunction)

    companion object {
        fun of(
            append: (function: KPFunction) -> Unit,
        ): KotlinFunctionContainerInternal = object : KotlinFunctionContainerInternal {
            override fun append(function: KPFunction) = append(function)
        }
    }
}

private val KotlinFunctionContainer.factory: KotlinFunctionFactory
    get() = this as KotlinFunctionFactory

private val KotlinFunctionContainer.internal: KotlinFunctionContainerInternal
    get() = when (this) {
        is KotlinFileScope -> functionContainer
        is KotlinTypeScope -> functionContainer
    }
