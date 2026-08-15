package io.github.diskria.poetesse.java

sealed interface JavaConstructorContainer : JavaConstructorFactory {
    operator fun JavaConstructorRef.unaryPlus() {
        internal.append(spec)
    }
}

fun JavaConstructorContainer.constructor(block: JavaConstructorScope.Block = {}) =
    +factory.constructor(block)

internal class JavaConstructorContainerInternal(val append: (constructor: JPMethod) -> Unit)

@PublishedApi
internal val JavaConstructorContainer.factory: JavaConstructorFactory
    get() = this as JavaConstructorFactory

private val JavaConstructorContainer.internal: JavaConstructorContainerInternal
    get() = when (this) {
        is JavaTypeScope -> constructorContainer
    }
