package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXClassName
import io.github.diskria.poetesse.interop.xType
import io.github.diskria.poetesse.interop.setNullable
import kotlin.reflect.KClass

sealed interface JavaFieldContainer : JavaFieldFactory {
    operator fun JavaFieldRef.unaryPlus() {
        internal.append(spec)
    }
}

fun JavaFieldContainer.field(
    name: String,
    type: XTypeName,
    block: JavaFieldScope.() -> Unit = {}
): String {
    +factory.field(name, type, block)
    return name
}

fun JavaFieldContainer.field(
    type: XTypeName,
    block: JavaFieldScope.() -> Unit = {}
) = EagerDelegate { name -> field(name, type, block) }

fun JavaFieldContainer.field(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    block: JavaFieldScope.() -> Unit = {}
) = field(name, type.xType().setNullable(nullable), block)

fun JavaFieldContainer.field(
    type: KClass<out Any>,
    nullable: Boolean = false,
    block: JavaFieldScope.() -> Unit = {}
) = EagerDelegate { name -> field(name, type, nullable, block) }

inline fun <reified T : Any> JavaFieldContainer.field(
    name: String,
    nullable: Boolean = false,
    noinline block: JavaFieldScope.() -> Unit = {}
) = field(name, T::class.asXClassName().setNullable(nullable), block)

inline fun <reified T : Any> JavaFieldContainer.field(
    nullable: Boolean = false,
    noinline block: JavaFieldScope.() -> Unit = {}
) = EagerDelegate { name -> field<T>(name, nullable, block) }

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
