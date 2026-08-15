package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XParameter
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXParameter
import kotlin.reflect.KClass

sealed interface KotlinParameterTrait : KotlinParameterFactory {
    operator fun KotlinParameterRef.unaryPlus(): XParameter {
        container.append(spec)
        return spec.asXParameter()
    }
}

fun KotlinParameterTrait.parameter(name: String, type: XTypeName, block: KotlinParameterScope.Block = {}) =
    +factory.parameter(name, type, block)

fun KotlinParameterTrait.parameter(type: XTypeName, block: KotlinParameterScope.Block = {}) =
    EagerDelegate { parameter(it, type, block) }

fun KotlinParameterTrait.parameter(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinParameterScope.Block = {}
) = +factory.parameter(name, type, nullable, block)

fun KotlinParameterTrait.parameter(
    type: KClass<*>, nullable: Boolean = false, block: KotlinParameterScope.Block = {}
) = EagerDelegate { parameter(it, type, nullable, block) }

inline fun <reified T> KotlinParameterTrait.parameter(
    name: String, nullable: Boolean = true, noinline block: KotlinParameterScope.Block = {}
) = +factory.parameter<T>(name, nullable, block)

inline fun <reified T : Any> KotlinParameterTrait.parameter(
    name: String, noinline block: KotlinParameterScope.Block = {}
) = +factory.parameter<T>(name, block)

inline fun <reified T> KotlinParameterTrait.parameter(
    nullable: Boolean = true, noinline block: KotlinParameterScope.Block = {}
) = EagerDelegate { parameter<T>(it, nullable, block) }

inline fun <reified T : Any> KotlinParameterTrait.parameter(noinline block: KotlinParameterScope.Block = {}) =
    parameter<T>(nullable = false, block)

internal class KotlinParameterContainer(val append: (parameter: KPParameter) -> Unit)

@PublishedApi
internal val KotlinParameterTrait.factory: KotlinParameterFactory
    get() = this as KotlinParameterFactory

private val KotlinParameterTrait.container: KotlinParameterContainer
    get() = when (this) {
        is KotlinPropertySetterScope -> parameterContainer
        is KotlinConstructorScope -> parameterContainer
        is KotlinFunctionScope -> parameterContainer
    }
