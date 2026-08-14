package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

interface KotlinPropertyFactory : PoetesseKotlinScope

fun KotlinPropertyFactory.property(name: String, type: XTypeName, block: KotlinPropertyScope.Block = {}) =
    KotlinPropertyRef(name) { KotlinPropertyScope.of(settings, name, type).apply(block).build() }

fun KotlinPropertyFactory.property(type: XTypeName, block: KotlinPropertyScope.Block = {}) =
    LazyDelegate { name -> property(name, type, block) }

fun KotlinPropertyFactory.property(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinPropertyScope.Block = {}
) = property(name, xType(type, nullable = nullable), block)

fun KotlinPropertyFactory.property(
    type: KClass<*>, nullable: Boolean = false, block: KotlinPropertyScope.Block = {}
) = LazyDelegate { name -> property(name, type, nullable, block) }

inline fun <reified T> KotlinPropertyFactory.property(
    name: String, nullable: Boolean = true, noinline block: KotlinPropertyScope.Block = {}
) = property(name, T::class, nullable, block)

inline fun <reified T : Any> KotlinPropertyFactory.property(
    name: String, noinline block: KotlinPropertyScope.Block = {}
) = property<T>(name, nullable = false, block)

inline fun <reified T> KotlinPropertyFactory.property(
    nullable: Boolean = true, noinline block: KotlinPropertyScope.Block = {}
) = LazyDelegate { name -> property<T>(name, nullable, block) }

inline fun <reified T : Any> KotlinPropertyFactory.property(noinline block: KotlinPropertyScope.Block = {}) =
    property<T>(nullable = false, block)
