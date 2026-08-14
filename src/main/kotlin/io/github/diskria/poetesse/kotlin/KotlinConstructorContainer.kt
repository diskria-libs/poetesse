package io.github.diskria.poetesse.kotlin

sealed interface KotlinConstructorContainer : KotlinConstructorFactory {

    operator fun KotlinConstructorRef.unaryPlus() {
        internal.append(spec, isPrimary)
    }

    fun constructor(primary: Boolean = false, block: KotlinConstructorScope.Block = {}) =
        +factory.constructor(primary, block)
}

internal interface KotlinConstructorContainerInternal {

    fun append(constructor: KPFunction, isPrimary: Boolean)

    companion object {
        fun of(
            append: (constructor: KPFunction, isPrimary: Boolean) -> Unit,
        ): KotlinConstructorContainerInternal = object : KotlinConstructorContainerInternal {
            override fun append(constructor: KPFunction, isPrimary: Boolean) = append(constructor, isPrimary)
        }
    }
}

private val KotlinConstructorContainer.factory: KotlinConstructorFactory
    get() = this as KotlinConstructorFactory

private val KotlinConstructorContainer.internal: KotlinConstructorContainerInternal
    get() = when (this) {
        is KotlinTypeScope -> constructorContainer
    }
