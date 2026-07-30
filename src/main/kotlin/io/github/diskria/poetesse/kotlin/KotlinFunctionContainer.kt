package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.MemberSpecHolder
import io.github.diskria.poetesse.XFunctionName

sealed interface KotlinFunctionContainer {

    fun fun_(name: String, block: KotlinFunctionScope.() -> Unit = {}): XFunctionName {
        internal.specHolderBuilder.addFunction(KotlinFunctionScope.of(name).apply(block).build())
        return XFunctionName.of(name)
    }
}

internal interface KotlinFunctionContainerInternal {

    val specHolderBuilder: MemberSpecHolder.Builder<*>

    companion object {
        fun of(
            specHolderBuilder: MemberSpecHolder.Builder<*>,
        ): KotlinFunctionContainerInternal = object : KotlinFunctionContainerInternal {
            override val specHolderBuilder: MemberSpecHolder.Builder<*> = specHolderBuilder
        }
    }
}

private val KotlinFunctionContainer.internal: KotlinFunctionContainerInternal
    get() = when (this) {
        is KotlinFileScope -> functionContainer
        is KotlinTypeScope -> functionContainer
    }
