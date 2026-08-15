package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import kotlin.reflect.KClass

sealed interface KotlinPropertyTrait : KotlinPropertyFactory {
    operator fun KotlinPropertyRef.unaryPlus(): String {
        container.append(spec)
        return name
    }
}

fun KotlinPropertyTrait.property(name: String, type: XTypeName, block: KotlinPropertyScope.Block = {}) =
    +factory.property(name, type, block)

fun KotlinPropertyTrait.property(type: XTypeName, block: KotlinPropertyScope.Block = {}) =
    EagerDelegate { property(it, type, block) }

fun KotlinPropertyTrait.property(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinPropertyScope.Block = {}
) = +factory.property(name, type, nullable, block)

fun KotlinPropertyTrait.property(
    type: KClass<*>, nullable: Boolean = false, block: KotlinPropertyScope.Block = {}
) = EagerDelegate { property(it, type, nullable, block) }

inline fun <reified T> KotlinPropertyTrait.property(
    name: String, nullable: Boolean = true, noinline block: KotlinPropertyScope.Block = {}
) = +factory.property<T>(name, nullable, block)

inline fun <reified T : Any> KotlinPropertyTrait.property(
    name: String, noinline block: KotlinPropertyScope.Block = {}
) = +factory.property<T>(name, block)

inline fun <reified T> KotlinPropertyTrait.property(
    nullable: Boolean = true, noinline block: KotlinPropertyScope.Block = {}
) = EagerDelegate { property<T>(it, nullable, block) }

inline fun <reified T : Any> KotlinPropertyTrait.property(noinline block: KotlinPropertyScope.Block = {}) =
    property<T>(nullable = false, block)

internal class KotlinPropertyContainer(val append: (property: KPProperty) -> Unit)

@PublishedApi
internal val KotlinPropertyTrait.factory: KotlinPropertyFactory
    get() = this as KotlinPropertyFactory

private val KotlinPropertyTrait.container: KotlinPropertyContainer
    get() = when (this) {
        is KotlinFileScope -> propertyContainer
        is KotlinTypeScope -> propertyContainer
    }
