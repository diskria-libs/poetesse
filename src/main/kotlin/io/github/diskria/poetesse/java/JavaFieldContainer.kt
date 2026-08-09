package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.nullable
import io.github.diskria.poetesse.interop.xClass
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

sealed interface JavaFieldContainer : JavaFieldFactory {

    operator fun JavaFieldRef.unaryPlus(): String {
        internal.append(spec)
        return name
    }
}

fun JavaFieldContainer.field(name: String, type: XTypeName, block: JavaFieldScope.() -> Unit = {}) =
    +factory.field(name, type, block)

fun JavaFieldContainer.field(type: XTypeName, block: JavaFieldScope.() -> Unit = {}) =
    EagerDelegate { name -> field(name, type, block) }

fun JavaFieldContainer.field(
    name: String, type: KClass<*>, nullable: Boolean = false, block: JavaFieldScope.() -> Unit = {}
) = field(name, type.xType(nullable = nullable), block)

fun JavaFieldContainer.field(type: KClass<*>, nullable: Boolean = false, block: JavaFieldScope.() -> Unit = {}) =
    EagerDelegate { name -> field(name, type, nullable, block) }

inline fun <reified T> JavaFieldContainer.field(
    name: String, nullable: Boolean = true, noinline block: JavaFieldScope.() -> Unit = {}
) = field(name, T::class.xClass(nullable = nullable), block)

inline fun <reified T : Any> JavaFieldContainer.field(name: String, noinline block: JavaFieldScope.() -> Unit = {}) =
    field<T>(name, nullable = false, block)

inline fun <reified T> JavaFieldContainer.field(
    nullable: Boolean = true, noinline block: JavaFieldScope.() -> Unit = {}
) = EagerDelegate { name -> field<T>(name, nullable, block) }

inline fun <reified T : Any> JavaFieldContainer.field(noinline block: JavaFieldScope.() -> Unit = {}) =
    field<T>(nullable = false, block)

internal interface JavaFieldContainerInternal {

    fun append(field: JPField)

    companion object {
        fun of(
            append: (field: JPField) -> Unit,
        ): JavaFieldContainerInternal = object : JavaFieldContainerInternal {
            override fun append(field: JPField) = append(field)
        }
    }
}

private val JavaFieldContainer.factory: JavaFieldFactory
    get() = this as JavaFieldFactory

private val JavaFieldContainer.internal: JavaFieldContainerInternal
    get() = when (this) {
        is JavaTypeScope -> fieldContainer
    }
