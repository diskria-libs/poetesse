package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XParameter
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXParameter
import kotlin.reflect.KClass

sealed interface KotlinContextParameterTrait : KotlinContextParameterFactory {
    operator fun KotlinContextParameterRef.unaryPlus(): XParameter {
        container.append(spec)
        return spec.asXParameter()
    }
}

fun KotlinContextParameterTrait.contextParameter(name: String, type: XTypeName) =
    +factory.contextParameter(name, type)

fun KotlinContextParameterTrait.contextParameter(type: XTypeName) =
    EagerDelegate { contextParameter(it, type) }

fun KotlinContextParameterTrait.contextParameter(name: String, type: KClass<*>, nullable: Boolean = false) =
    +factory.contextParameter(name, type, nullable)

fun KotlinContextParameterTrait.contextParameter(type: KClass<*>, nullable: Boolean = false) =
    EagerDelegate { contextParameter(it, type, nullable) }

inline fun <reified T> KotlinContextParameterTrait.contextParameter(name: String, nullable: Boolean = true) =
    +factory.contextParameter<T>(name, nullable)

inline fun <reified T : Any> KotlinContextParameterTrait.contextParameter(name: String) =
    +factory.contextParameter<T>(name)

inline fun <reified T> KotlinContextParameterTrait.contextParameter(nullable: Boolean = true) =
    EagerDelegate { contextParameter<T>(it, nullable) }

inline fun <reified T : Any> KotlinContextParameterTrait.contextParameter() = contextParameter<T>(nullable = false)

internal class KotlinContextParameterContainer(val append: (parameter: KPContextParameter) -> Unit)

@PublishedApi
internal val KotlinContextParameterTrait.factory: KotlinContextParameterFactory
    get() = this as KotlinContextParameterFactory

private val KotlinContextParameterTrait.container: KotlinContextParameterContainer
    get() = when (this) {
        is KotlinPropertyScope -> contextParameterContainer
        is KotlinFunctionScope -> contextParameterContainer
    }
