package io.github.diskria.poetesse.kotlin

sealed interface KotlinConstructorContainer : KotlinConstructorFactory {
    operator fun KotlinConstructorRef.unaryPlus() {
        internal.append(build(internal.outerBuilder), isPrimary)
    }
}

fun KotlinConstructorContainer.constructor(primary: Boolean = true, block: KotlinConstructorScope.Block = {}) =
    +factory.constructor(primary, block)

internal class KotlinConstructorContainerInternal(
    val outerBuilder: KPTypeBuilder,
    val append: (constructor: KPFunction, isPrimary: Boolean) -> Unit
)

@PublishedApi
internal val KotlinConstructorContainer.factory: KotlinConstructorFactory
    get() = this as KotlinConstructorFactory

private val KotlinConstructorContainer.internal: KotlinConstructorContainerInternal
    get() = when (this) {
        is KotlinTypeScope -> constructorContainer
    }
