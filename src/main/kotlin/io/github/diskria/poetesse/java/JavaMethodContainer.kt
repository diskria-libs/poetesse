package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate

sealed interface JavaMethodContainer : JavaMethodFactory {
    operator fun JavaMethodRef.unaryPlus(): String {
        internal.append(spec)
        return name
    }
}

fun JavaMethodContainer.method(name: String, block: JavaMethodScope.Block = {}) =
    +factory.method(name, block)

fun JavaMethodContainer.method(block: JavaMethodScope.Block = {}) =
    EagerDelegate { name -> method(name, block) }

internal interface JavaMethodContainerInternal {

    fun append(method: JPMethod)

    companion object {
        fun of(
            append: (method: JPMethod) -> Unit,
        ): JavaMethodContainerInternal = object : JavaMethodContainerInternal {
            override fun append(method: JPMethod) = append(method)
        }
    }
}

@PublishedApi
internal val JavaMethodContainer.factory: JavaMethodFactory
    get() = this as JavaMethodFactory

private val JavaMethodContainer.internal: JavaMethodContainerInternal
    get() = when (this) {
        is JavaTypeScope -> methodContainer
    }
