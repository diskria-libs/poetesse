package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XParameter
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXParameter
import kotlin.reflect.KClass

sealed interface JavaParameterContainer : JavaParameterFactory {
    operator fun JavaParameterRef.unaryPlus(): XParameter {
        internal.append(spec)
        return spec.asXParameter()
    }
}

fun JavaParameterContainer.parameter(name: String, type: XTypeName, block: JavaParameterScope.Block = {}) =
    +factory.parameter(name, type, block)

fun JavaParameterContainer.parameter(type: XTypeName, block: JavaParameterScope.Block = {}) =
    EagerDelegate { name -> parameter(name, type, block) }

fun JavaParameterContainer.parameter(
    name: String, type: KClass<*>, nullable: Boolean = false, block: JavaParameterScope.Block = {}
) = +factory.parameter(name, type, nullable, block)

fun JavaParameterContainer.parameter(
    type: KClass<*>, nullable: Boolean = false, block: JavaParameterScope.Block = {}
) = EagerDelegate { name -> parameter(name, type, nullable, block) }

inline fun <reified T> JavaParameterContainer.parameter(
    name: String, nullable: Boolean = true, noinline block: JavaParameterScope.Block = {}
) = +factory.parameter<T>(name, nullable, block)

inline fun <reified T : Any> JavaParameterContainer.parameter(
    name: String, noinline block: JavaParameterScope.Block = {}
) = +factory.parameter<T>(name, block)

inline fun <reified T> JavaParameterContainer.parameter(
    nullable: Boolean = true, noinline block: JavaParameterScope.Block = {}
) = EagerDelegate { name -> parameter<T>(name, nullable, block) }

inline fun <reified T : Any> JavaParameterContainer.parameter(noinline block: JavaParameterScope.Block = {}) =
    parameter<T>(nullable = false, block)

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

@PublishedApi
internal val JavaParameterContainer.factory: JavaParameterFactory
    get() = this as JavaParameterFactory

private val JavaParameterContainer.internal: JavaParameterContainerInternal
    get() = when (this) {
        is JavaConstructorScope -> parameterContainer
        is JavaMethodScope -> parameterContainer
    }
