package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.XFunctionName

class JavaMethodContainerScope private constructor() {

    sealed interface External : JavaMethodFactory {

        fun method(name: String, block: JavaMethodScope.() -> Unit = {}): XFunctionName {
            internal.append((this as JavaMethodFactory).method(name, block).spec)
            return XFunctionName.of(name)
        }

        fun method(block: JavaMethodScope.() -> Unit = {}): EagerDelegate<XFunctionName> =
            EagerDelegate { name -> method(name, block) }

        operator fun JavaDeferredMethod.unaryPlus(): XFunctionName {
            internal.append(spec)
            return XFunctionName.of(name)
        }
    }

    internal interface Internal {

        fun append(method: JPMethod)

        companion object {
            fun of(
                append: (method: JPMethod) -> Unit,
            ): Internal = object : Internal {
                override fun append(method: JPMethod) = append(method)
            }
        }
    }
}

private val JavaMethodContainerScope.External.internal: JavaMethodContainerScope.Internal
    get() = when (this) {
        is JavaTypeScope -> methodContainerInternalScope
    }
