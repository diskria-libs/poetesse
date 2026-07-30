package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.MemberSpecHolder
import io.github.diskria.poetesse.XFunctionName

class KotlinFunctionContainerScope private constructor() {

    sealed interface External {
        fun fun_(name: String, block: KotlinFunctionScope.() -> Unit = {}): XFunctionName {
            internal.specHolderBuilder.addFunction(KotlinFunctionScope.of(name).apply(block).build())
            return XFunctionName.of(name)
        }
    }

    internal interface Internal {

        val specHolderBuilder: MemberSpecHolder.Builder<*>

        companion object {
            fun of(
                specHolderBuilder: MemberSpecHolder.Builder<*>,
            ): Internal = object : Internal {
                override val specHolderBuilder: MemberSpecHolder.Builder<*> = specHolderBuilder
            }
        }
    }
}

private val KotlinFunctionContainerScope.External.internal: KotlinFunctionContainerScope.Internal
    get() = when (this) {
        is KotlinFileScope -> functionContainerInternalScope
        is KotlinTypeScope -> functionContainerInternalScope
    }
