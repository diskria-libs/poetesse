package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

interface KotlinParameterFactory : PoetesseKotlinScope

fun KotlinParameterFactory.parameter(name: String, type: XTypeName, block: KotlinParameterScope.Block = {}) =
    KotlinParameterRef(name) { KotlinParameterScope.of(name, type).apply(block).build() }

fun KotlinParameterFactory.parameter(type: XTypeName, block: KotlinParameterScope.Block = {}) =
    LazyDelegate { parameter(it, type, block) }

fun KotlinParameterFactory.parameter(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinParameterScope.Block = {}
) = parameter(name, xType(type, nullable = nullable), block)

fun KotlinParameterFactory.parameter(
    type: KClass<*>, nullable: Boolean = false, block: KotlinParameterScope.Block = {}
) = LazyDelegate { parameter(it, type, nullable, block) }

inline fun <reified T> KotlinParameterFactory.parameter(
    name: String, nullable: Boolean = true, noinline block: KotlinParameterScope.Block = {}
) = parameter(name, T::class, nullable, block)

inline fun <reified T : Any> KotlinParameterFactory.parameter(
    name: String, noinline block: KotlinParameterScope.Block = {}
) = parameter<T>(name, nullable = false, block)

inline fun <reified T> KotlinParameterFactory.parameter(
    nullable: Boolean = true, noinline block: KotlinParameterScope.Block = {}
) = LazyDelegate { parameter<T>(it, nullable, block) }

inline fun <reified T : Any> KotlinParameterFactory.parameter(noinline block: KotlinParameterScope.Block = {}) =
    parameter<T>(nullable = false, block)
