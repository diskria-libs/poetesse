package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

interface JavaFieldFactory : PoetesseJavaScope

fun JavaFieldFactory.field(name: String, type: XTypeName, block: JavaFieldScope.Block = {}) =
    JavaFieldRef(name) { JavaFieldScope.of(name, type).apply(block).build() }

fun JavaFieldFactory.field(type: XTypeName, block: JavaFieldScope.Block = {}) =
    LazyDelegate { field(it, type, block) }

fun JavaFieldFactory.field(
    name: String, type: KClass<*>, nullable: Boolean = false, block: JavaFieldScope.Block = {}
) = field(name, xType(type, nullable = nullable), block)

fun JavaFieldFactory.field(type: KClass<*>, nullable: Boolean = false, block: JavaFieldScope.Block = {}) =
    LazyDelegate { field(it, type, nullable, block) }

inline fun <reified T> JavaFieldFactory.field(
    name: String, nullable: Boolean = true, noinline block: JavaFieldScope.Block = {}
) = field(name, T::class, nullable, block)

inline fun <reified T : Any> JavaFieldFactory.field(name: String, noinline block: JavaFieldScope.Block = {}) =
    field<T>(name, nullable = false, block)

inline fun <reified T> JavaFieldFactory.field(nullable: Boolean = true, noinline block: JavaFieldScope.Block = {}) =
    LazyDelegate { field<T>(it, nullable, block) }

inline fun <reified T : Any> JavaFieldFactory.field(noinline block: JavaFieldScope.Block = {}) =
    field<T>(nullable = false, block)
