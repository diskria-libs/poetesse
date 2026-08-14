package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

interface KotlinPropertyFactory : PoetesseKotlinScope

fun KotlinPropertyFactory.property(name: String, type: XTypeName<*, *>, block: KotlinPropertyScope.() -> Unit = {}) =
    KotlinPropertyRef(name) { KotlinPropertyScope.of(settings, name, type).apply(block).build() }

fun KotlinPropertyFactory.property(type: XTypeName<*, *>, block: KotlinPropertyScope.() -> Unit = {}) =
    LazyDelegate { name -> property(name, type, block) }

fun KotlinPropertyFactory.property(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinPropertyScope.() -> Unit = {}
) = property(name, xType(type, nullable = nullable), block)

fun KotlinPropertyFactory.property(
    type: KClass<*>,
    nullable: Boolean = false,
    block: KotlinPropertyScope.() -> Unit = {}
) = LazyDelegate { name -> property(name, type, nullable, block) }

inline fun <reified T> KotlinPropertyFactory.property(
    name: String, nullable: Boolean = true, noinline block: KotlinPropertyScope.() -> Unit = {}
) = property(name, T::class, nullable, block)

inline fun <reified T : Any> KotlinPropertyFactory.property(
    name: String,
    noinline block: KotlinPropertyScope.() -> Unit = {}
) = property<T>(name, nullable = false, block)

inline fun <reified T> KotlinPropertyFactory.property(
    nullable: Boolean = true, noinline block: KotlinPropertyScope.() -> Unit = {}
) = LazyDelegate { name -> property<T>(name, nullable, block) }

inline fun <reified T : Any> KotlinPropertyFactory.property(noinline block: KotlinPropertyScope.() -> Unit = {}) =
    property<T>(nullable = false, block)
