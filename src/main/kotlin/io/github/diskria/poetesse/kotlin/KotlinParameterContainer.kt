package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XParameter
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXParameter
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

sealed interface KotlinParameterContainer : KotlinParameterFactory {

    operator fun KotlinParameterRef.unaryPlus(): XParameter {
        internal.append(spec)
        return spec.asXParameter()
    }
}

fun KotlinParameterContainer.parameter(
    name: String,
    type: XTypeName<*, *>,
    block: KotlinParameterScope.() -> Unit = {}
) = +factory.parameter(name, type, block)

fun KotlinParameterContainer.parameter(type: XTypeName<*, *>, block: KotlinParameterScope.() -> Unit = {}) =
    EagerDelegate { name -> parameter(name, type, block) }

fun KotlinParameterContainer.parameter(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinParameterScope.() -> Unit = {}
) = parameter(name, xType(type, nullable = nullable), block)

fun KotlinParameterContainer.parameter(
    type: KClass<*>, nullable: Boolean = false, block: KotlinParameterScope.() -> Unit = {}
) = EagerDelegate { name -> parameter(name, type, nullable, block) }

inline fun <reified T> KotlinParameterContainer.parameter(
    name: String, nullable: Boolean = true, noinline block: KotlinParameterScope.() -> Unit = {}
) = parameter(name, T::class, nullable, block)

inline fun <reified T : Any> KotlinParameterContainer.parameter(
    name: String, noinline block: KotlinParameterScope.() -> Unit = {}
) = parameter<T>(name, nullable = false, block)

inline fun <reified T> KotlinParameterContainer.parameter(
    nullable: Boolean = true, noinline block: KotlinParameterScope.() -> Unit = {}
) = EagerDelegate { name -> parameter<T>(name, nullable, block) }

inline fun <reified T : Any> KotlinParameterContainer.parameter(noinline block: KotlinParameterScope.() -> Unit = {}) =
    parameter<T>(nullable = false, block)

internal interface KotlinParameterContainerInternal {

    fun append(parameter: KPParameter)

    companion object {
        fun of(
            append: (parameter: KPParameter) -> Unit,
        ): KotlinParameterContainerInternal = object : KotlinParameterContainerInternal {
            override fun append(parameter: KPParameter) = append(parameter)
        }
    }
}

private val KotlinParameterContainer.factory: KotlinParameterFactory
    get() = this as KotlinParameterFactory

private val KotlinParameterContainer.internal: KotlinParameterContainerInternal
    get() = when (this) {
        is KotlinPropertySetterScope -> parameterContainer
        is KotlinConstructorScope -> parameterContainer
        is KotlinFunctionScope -> parameterContainer
    }
