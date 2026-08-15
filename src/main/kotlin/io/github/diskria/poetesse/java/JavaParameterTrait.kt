package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XParameter
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXParameter
import kotlin.reflect.KClass

sealed interface JavaParameterTrait : JavaParameterFactory {
    operator fun JavaParameterRef.unaryPlus(): XParameter {
        container.append(spec)
        return spec.asXParameter()
    }
}

fun JavaParameterTrait.parameter(name: String, type: XTypeName, block: JavaParameterScope.Block = {}) =
    +factory.parameter(name, type, block)

fun JavaParameterTrait.parameter(type: XTypeName, block: JavaParameterScope.Block = {}) =
    EagerDelegate { parameter(it, type, block) }

fun JavaParameterTrait.parameter(
    name: String, type: KClass<*>, nullable: Boolean = false, block: JavaParameterScope.Block = {}
) = +factory.parameter(name, type, nullable, block)

fun JavaParameterTrait.parameter(
    type: KClass<*>, nullable: Boolean = false, block: JavaParameterScope.Block = {}
) = EagerDelegate { parameter(it, type, nullable, block) }

inline fun <reified T> JavaParameterTrait.parameter(
    name: String, nullable: Boolean = true, noinline block: JavaParameterScope.Block = {}
) = +factory.parameter<T>(name, nullable, block)

inline fun <reified T : Any> JavaParameterTrait.parameter(
    name: String, noinline block: JavaParameterScope.Block = {}
) = +factory.parameter<T>(name, block)

inline fun <reified T> JavaParameterTrait.parameter(
    nullable: Boolean = true, noinline block: JavaParameterScope.Block = {}
) = EagerDelegate { parameter<T>(it, nullable, block) }

inline fun <reified T : Any> JavaParameterTrait.parameter(noinline block: JavaParameterScope.Block = {}) =
    parameter<T>(nullable = false, block)

internal class JavaParameterContainer(val append: (parameter: JPParameter) -> Unit)

@PublishedApi
internal val JavaParameterTrait.factory: JavaParameterFactory
    get() = this as JavaParameterFactory

private val JavaParameterTrait.container: JavaParameterContainer
    get() = when (this) {
        is JavaConstructorScope -> parameterContainer
        is JavaMethodScope -> parameterContainer
    }
