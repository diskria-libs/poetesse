package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import kotlin.reflect.KClass

sealed interface JavaFieldTrait : JavaFieldFactory {
    operator fun JavaFieldRef.unaryPlus(): String {
        container.append(spec)
        return name
    }
}

fun JavaFieldTrait.field(name: String, type: XTypeName, block: JavaFieldScope.Block = {}) =
    +factory.field(name, type, block)

fun JavaFieldTrait.field(type: XTypeName, block: JavaFieldScope.Block = {}) =
    EagerDelegate { field(it, type, block) }

fun JavaFieldTrait.field(
    name: String, type: KClass<*>, nullable: Boolean = false, block: JavaFieldScope.Block = {}
) = +factory.field(name, type, nullable, block)

fun JavaFieldTrait.field(type: KClass<*>, nullable: Boolean = false, block: JavaFieldScope.Block = {}) =
    EagerDelegate { field(it, type, nullable, block) }

inline fun <reified T> JavaFieldTrait.field(
    name: String, nullable: Boolean = true, noinline block: JavaFieldScope.Block = {}
) = +factory.field<T>(name, nullable, block)

inline fun <reified T : Any> JavaFieldTrait.field(name: String, noinline block: JavaFieldScope.Block = {}) =
    +factory.field<T>(name, block)

inline fun <reified T> JavaFieldTrait.field(nullable: Boolean = true, noinline block: JavaFieldScope.Block = {}) =
    EagerDelegate { field<T>(it, nullable, block) }

inline fun <reified T : Any> JavaFieldTrait.field(noinline block: JavaFieldScope.Block = {}) =
    field<T>(nullable = false, block)

internal class JavaFieldContainer(val append: (field: JPField) -> Unit)

@PublishedApi
internal val JavaFieldTrait.factory: JavaFieldFactory
    get() = this as JavaFieldFactory

private val JavaFieldTrait.container: JavaFieldContainer
    get() = when (this) {
        is JavaTypeScope -> fieldContainer
    }
