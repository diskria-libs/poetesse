package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import kotlin.reflect.KClass

sealed interface JavaFieldContainer : JavaFieldFactory {
    operator fun JavaFieldRef.unaryPlus() {
        internal.append(spec)
    }
}

fun JavaFieldContainer.field(
    name: String,
    type: XTypeName,
    interop: Boolean = true,
    block: JavaFieldScope.() -> Unit = {}
) = +factory.field(name, type, interop, block)

fun JavaFieldContainer.field(
    type: XTypeName,
    interop: Boolean = true,
    block: JavaFieldScope.() -> Unit = {}
) = EagerDelegate { name -> field(name, type, interop, block) }

fun JavaFieldContainer.field(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    block: JavaFieldScope.() -> Unit = {}
) = field(name, XTypeName.of(type, nullable), interop, block)

fun JavaFieldContainer.field(
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    block: JavaFieldScope.() -> Unit = {}
) = EagerDelegate { name -> field(name, type, nullable, interop, block) }

inline fun <reified T : Any> JavaFieldContainer.field(
    name: String,
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline block: JavaFieldScope.() -> Unit = {}
) = field(name, XTypeName.of<T>(nullable), interop, block)

inline fun <reified T : Any> JavaFieldContainer.field(
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline block: JavaFieldScope.() -> Unit = {}
) = EagerDelegate { name -> field<T>(name, nullable, interop, block) }

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
