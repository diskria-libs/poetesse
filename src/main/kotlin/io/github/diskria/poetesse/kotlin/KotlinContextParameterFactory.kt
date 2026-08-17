package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

interface KotlinContextParameterFactory : PoetesseKotlinScope

fun KotlinContextParameterFactory.contextParameter(name: String, type: XTypeName) =
    KotlinContextParameterRef(name) { KotlinContextParameterScope.of(name, type).build() }

fun KotlinContextParameterFactory.contextParameter(type: XTypeName) =
    LazyDelegate { contextParameter(it, type) }

fun KotlinContextParameterFactory.contextParameter(name: String, type: KClass<*>, nullable: Boolean = false) =
    contextParameter(name, xType(type, nullable = nullable))

fun KotlinContextParameterFactory.contextParameter(type: KClass<*>, nullable: Boolean = false) =
    LazyDelegate { contextParameter(it, type, nullable) }

inline fun <reified T> KotlinContextParameterFactory.contextParameter(name: String, nullable: Boolean = true) =
    contextParameter(name, T::class, nullable)

inline fun <reified T : Any> KotlinContextParameterFactory.contextParameter(name: String) =
    contextParameter<T>(name, nullable = false)

inline fun <reified T> KotlinContextParameterFactory.contextParameter(nullable: Boolean = true) =
    LazyDelegate { contextParameter<T>(it, nullable) }

inline fun <reified T : Any> KotlinContextParameterFactory.contextParameter() = contextParameter<T>(nullable = false)
