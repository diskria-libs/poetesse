package io.github.diskria.poetesse.kotlin

sealed interface KotlinConstructorTrait : KotlinConstructorFactory {
    operator fun KotlinConstructorRef.unaryPlus() {
        container.append(build(container.outerBuilder), isPrimary)
    }
}

fun KotlinConstructorTrait.constructor(primary: Boolean = true, block: KotlinConstructorScope.Block = {}) =
    +factory.constructor(primary, block)

internal class KotlinConstructorContainer(
    val outerBuilder: KPTypeBuilder,
    val append: (constructor: KPFunction, isPrimary: Boolean) -> Unit
)

@PublishedApi
internal val KotlinConstructorTrait.factory: KotlinConstructorFactory
    get() = this as KotlinConstructorFactory

private val KotlinConstructorTrait.container: KotlinConstructorContainer
    get() = when (this) {
        is KotlinTypeScope -> constructorContainer
    }
