package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate

sealed interface JavaMethodTrait : JavaMethodFactory {
    operator fun JavaMethodRef.unaryPlus(): String {
        container.append(spec)
        return name
    }
}

fun JavaMethodTrait.method(name: String, block: JavaMethodScope.Block = {}) =
    +factory.method(name, block)

fun JavaMethodTrait.method(block: JavaMethodScope.Block = {}) =
    EagerDelegate { method(it, block) }

internal class JavaMethodContainer(val append: (method: JPMethod) -> Unit)

@PublishedApi
internal val JavaMethodTrait.factory: JavaMethodFactory
    get() = this as JavaMethodFactory

private val JavaMethodTrait.container: JavaMethodContainer
    get() = when (this) {
        is JavaTypeScope -> methodContainer
    }
