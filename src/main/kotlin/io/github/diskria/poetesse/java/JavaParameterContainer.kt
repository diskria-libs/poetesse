package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import kotlin.reflect.KClass

sealed interface JavaParameterContainer : JavaParameterFactory {
    operator fun JavaParameterRef.unaryPlus() {
        internal.append(spec)
    }
}

fun JavaParameterContainer.parameter(
    name: String,
    type: XTypeName,
    interop: Boolean = true,
    block: JavaParameterScope.() -> Unit = {}
): String {
    +factory.parameter(name, type, interop, block)
    return name
}

fun JavaParameterContainer.parameter(
    type: XTypeName,
    interop: Boolean = true,
    block: JavaParameterScope.() -> Unit = {}
) = EagerDelegate { name -> parameter(name, type, interop, block) }

fun JavaParameterContainer.parameter(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    block: JavaParameterScope.() -> Unit = {}
) = parameter(name, XTypeName.of(type, nullable), interop, block)

fun JavaParameterContainer.parameter(
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    block: JavaParameterScope.() -> Unit = {}
) = EagerDelegate { name -> parameter(name, type, nullable, interop, block) }

inline fun <reified T : Any> JavaParameterContainer.parameter(
    name: String,
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline block: JavaParameterScope.() -> Unit = {}
) = parameter(name, XTypeName.of<T>(nullable), interop, block)

inline fun <reified T : Any> JavaParameterContainer.parameter(
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline block: JavaParameterScope.() -> Unit = {}
) = EagerDelegate { name -> parameter<T>(name, nullable, interop, block) }

internal interface JavaParameterContainerInternal {

    fun append(parameter: JPParameter)

    companion object {
        fun of(
            append: (parameter: JPParameter) -> Unit,
        ): JavaParameterContainerInternal = object : JavaParameterContainerInternal {
            override fun append(parameter: JPParameter) = append(parameter)
        }
    }
}

private val JavaParameterContainer.factory: JavaParameterFactory
    get() = this as JavaParameterFactory

private val JavaParameterContainer.internal: JavaParameterContainerInternal
    get() = when (this) {
        is JavaConstructorScope -> parameterContainer
        is JavaMethodScope -> parameterContainer
    }
