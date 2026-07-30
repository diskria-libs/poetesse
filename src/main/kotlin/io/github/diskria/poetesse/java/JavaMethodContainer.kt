package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.XFunctionName

sealed interface JavaMethodContainer : JavaMethodFactory {

    operator fun JavaDeferredMethod.unaryPlus(): XFunctionName {
        internal.append(spec)
        return XFunctionName.of(name)
    }

    fun method(name: String, block: JavaMethodScope.() -> Unit = {}): XFunctionName =
        +factory.method(name, block)

    fun method(block: JavaMethodScope.() -> Unit = {}): EagerDelegate<XFunctionName> =
        EagerDelegate { name -> method(name, block) }
}

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

private val JavaMethodContainer.factory: JavaMethodFactory
    get() = this as JavaMethodFactory

private val JavaMethodContainer.internal: JavaMethodContainerInternal
    get() = when (this) {
        is JavaTypeScope -> methodContainer
    }
