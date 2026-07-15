package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.XFunctionName

class JavaMethodContainerScope private constructor() {

    sealed interface External {

        fun method(name: String, block: JavaMethodScope.() -> Unit = {}): XFunctionName {
            internal.append(JavaMethodScope.of(name).apply(block).build())
            return XFunctionName.of(name)
        }
    }

    internal interface Internal {

        fun append(method: JPMethod)

        companion object {
            internal fun of(
                append: (method: JPMethod) -> Unit,
            ): Internal = object : Internal {
                override fun append(method: JPMethod) = append(method)
            }
        }
    }
}

@PublishedApi
internal val JavaMethodContainerScope.External.internal: JavaMethodContainerScope.Internal
    get() = when (this) {
        is JavaTypeScope -> methodContainerInternalScope
    }
