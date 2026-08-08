package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.*
import kotlin.reflect.KClass

sealed interface JavaParameterContainer : JavaParameterFactory {
    operator fun JavaParameterRef.unaryPlus(): XParameter {
        internal.append(spec)
        return spec.asXParameter()
    }
}

fun JavaParameterContainer.parameter(
    name: String,
    type: XTypeName,
    block: JavaParameterScope.() -> Unit = {}
) = +factory.parameter(name, type, block)

fun JavaParameterContainer.parameter(
    type: XTypeName,
    block: JavaParameterScope.() -> Unit = {}
) = EagerDelegate { name -> parameter(name, type, block) }

fun JavaParameterContainer.parameter(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    block: JavaParameterScope.() -> Unit = {}
) = parameter(name, type.xType().setNullable(nullable), block)

fun JavaParameterContainer.parameter(
    type: KClass<out Any>,
    nullable: Boolean = false,
    block: JavaParameterScope.() -> Unit = {}
) = EagerDelegate { name -> parameter(name, type, nullable, block) }

inline fun <reified T : Any> JavaParameterContainer.parameter(
    name: String,
    nullable: Boolean = false,
    noinline block: JavaParameterScope.() -> Unit = {}
) = parameter(name, T::class.asXClassName().setNullable(nullable), block)

inline fun <reified T : Any> JavaParameterContainer.parameter(
    nullable: Boolean = false,
    noinline block: JavaParameterScope.() -> Unit = {}
) = EagerDelegate { name -> parameter<T>(name, nullable, block) }

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
