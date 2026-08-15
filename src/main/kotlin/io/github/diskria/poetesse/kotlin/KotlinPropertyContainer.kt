package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import kotlin.reflect.KClass

sealed interface KotlinPropertyContainer : KotlinPropertyFactory {
    operator fun KotlinPropertyRef.unaryPlus(): String {
        internal.append(spec)
        return name
    }
}

fun KotlinPropertyContainer.property(name: String, type: XTypeName, block: KotlinPropertyScope.Block = {}) =
    +factory.property(name, type, block)

fun KotlinPropertyContainer.property(type: XTypeName, block: KotlinPropertyScope.Block = {}) =
    EagerDelegate { name -> property(name, type, block) }

fun KotlinPropertyContainer.property(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinPropertyScope.Block = {}
) = +factory.property(name, type, nullable, block)

fun KotlinPropertyContainer.property(
    type: KClass<*>, nullable: Boolean = false, block: KotlinPropertyScope.Block = {}
) = EagerDelegate { name -> property(name, type, nullable, block) }

inline fun <reified T> KotlinPropertyContainer.property(
    name: String, nullable: Boolean = true, noinline block: KotlinPropertyScope.Block = {}
) = +factory.property<T>(name, nullable, block)

inline fun <reified T : Any> KotlinPropertyContainer.property(
    name: String, noinline block: KotlinPropertyScope.Block = {}
) = +factory.property<T>(name, block)

inline fun <reified T> KotlinPropertyContainer.property(
    nullable: Boolean = true, noinline block: KotlinPropertyScope.Block = {}
) = EagerDelegate { name -> property<T>(name, nullable, block) }

inline fun <reified T : Any> KotlinPropertyContainer.property(noinline block: KotlinPropertyScope.Block = {}) =
    property<T>(nullable = false, block)

internal interface KotlinPropertyContainerInternal {

    fun append(property: KPProperty)

    companion object {
        fun of(
            append: (property: KPProperty) -> Unit,
        ): KotlinPropertyContainerInternal = object : KotlinPropertyContainerInternal {
            override fun append(property: KPProperty) = append(property)
        }
    }
}

@PublishedApi
internal val KotlinPropertyContainer.factory: KotlinPropertyFactory
    get() = this as KotlinPropertyFactory

private val KotlinPropertyContainer.internal: KotlinPropertyContainerInternal
    get() = when (this) {
        is KotlinFileScope -> propertyContainer
        is KotlinTypeScope -> propertyContainer
    }
