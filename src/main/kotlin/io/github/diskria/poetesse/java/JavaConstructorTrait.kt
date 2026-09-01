package io.github.diskria.poetesse.java

sealed interface JavaConstructorTrait : JavaConstructorFactory {
    operator fun JavaConstructorRef.unaryPlus() {
        container.append(spec)
    }
}

fun JavaConstructorTrait.constructor(block: JavaConstructorScope.Block = {}) =
    +factory.constructor(block)

internal class JavaConstructorContainer(val append: (constructor: JPMethod) -> Unit)

@PublishedApi
internal val JavaConstructorTrait.factory: JavaConstructorFactory
    get() = this as JavaConstructorFactory

private val JavaConstructorTrait.container: JavaConstructorContainer
    get() = when (this) {
        is AbstractJavaTypeScope -> constructorContainer
    }
