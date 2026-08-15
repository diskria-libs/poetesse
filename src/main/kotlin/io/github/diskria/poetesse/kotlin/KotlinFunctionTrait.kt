package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate

sealed interface KotlinFunctionTrait : KotlinFunctionFactory {
    operator fun KotlinFunctionRef.unaryPlus(): String {
        container.append(spec)
        return name
    }
}

fun KotlinFunctionTrait.function(name: String, block: KotlinFunctionScope.Block = {}) =
    +factory.function(name, block)

fun KotlinFunctionTrait.function(block: KotlinFunctionScope.Block = {}) =
    EagerDelegate { function(it, block) }

internal class KotlinFunctionContainer(val append: (function: KPFunction) -> Unit)

@PublishedApi
internal val KotlinFunctionTrait.factory: KotlinFunctionFactory
    get() = this as KotlinFunctionFactory

private val KotlinFunctionTrait.container: KotlinFunctionContainer
    get() = when (this) {
        is KotlinFileScope -> functionContainer
        is KotlinTypeScope -> functionContainer
    }
