package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

interface JavaParameterFactory : PoetesseScope

fun JavaParameterFactory.parameter(name: String, type: XTypeName<*, *>, block: JavaParameterScope.() -> Unit) =
    JavaParameterRef(name) { JavaParameterScope.of(settings, name, type).apply(block).build() }

fun JavaParameterFactory.parameter(type: XTypeName<*, *>, block: JavaParameterScope.() -> Unit = {}) =
    LazyDelegate { name -> parameter(name, type, block) }

fun JavaParameterFactory.parameter(
    name: String, type: KClass<*>, nullable: Boolean = false, block: JavaParameterScope.() -> Unit = {}
) = parameter(name, xType(type, nullable = nullable), block)

fun JavaParameterFactory.parameter(
    type: KClass<*>, nullable: Boolean = false, block: JavaParameterScope.() -> Unit = {}
) = LazyDelegate { name -> parameter(name, type, nullable, block) }

inline fun <reified T> JavaParameterFactory.parameter(
    name: String, nullable: Boolean = true, noinline block: JavaParameterScope.() -> Unit = {}
) = parameter(name, T::class, nullable, block)

inline fun <reified T : Any> JavaParameterFactory.parameter(
    name: String, noinline block: JavaParameterScope.() -> Unit = {}
) = parameter<T>(name, nullable = false, block)

inline fun <reified T> JavaParameterFactory.parameter(
    nullable: Boolean = true, noinline block: JavaParameterScope.() -> Unit = {}
) = LazyDelegate { name -> parameter<T>(name, nullable, block) }

inline fun <reified T : Any> JavaParameterFactory.parameter(noinline block: JavaParameterScope.() -> Unit = {}) =
    parameter<T>(nullable = false, block)
