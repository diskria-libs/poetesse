package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

interface KotlinParameterFactory

fun KotlinParameterFactory.parameter(name: String, type: XTypeName, block: KotlinParameterScope.() -> Unit) =
    KotlinParameterRef(name) { KotlinParameterScope.of(name, type).apply(block).build() }

fun KotlinParameterFactory.parameter(type: XTypeName, block: KotlinParameterScope.() -> Unit = {}) =
    LazyDelegate { name -> parameter(name, type, block) }

fun KotlinParameterFactory.parameter(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinParameterScope.() -> Unit = {}
) = parameter(name, type.xType(nullable = nullable), block)

fun KotlinParameterFactory.parameter(
    type: KClass<*>, nullable: Boolean = false, block: KotlinParameterScope.() -> Unit = {}
) = LazyDelegate { name -> parameter(name, type, nullable, block) }

inline fun <reified T> KotlinParameterFactory.parameter(
    name: String, nullable: Boolean = true, noinline block: KotlinParameterScope.() -> Unit = {}
) = parameter(name, T::class, nullable, block)

inline fun <reified T : Any> KotlinParameterFactory.parameter(
    name: String, noinline block: KotlinParameterScope.() -> Unit = {}
) = parameter<T>(name, nullable = false, block)

inline fun <reified T> KotlinParameterFactory.parameter(
    nullable: Boolean = true, noinline block: KotlinParameterScope.() -> Unit = {}
) = LazyDelegate { name -> parameter<T>(name, nullable, block) }

inline fun <reified T : Any> KotlinParameterFactory.parameter(noinline block: KotlinParameterScope.() -> Unit = {}) =
    parameter<T>(nullable = false, block)
