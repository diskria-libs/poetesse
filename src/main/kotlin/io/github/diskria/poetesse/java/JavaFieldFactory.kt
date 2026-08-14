package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

interface JavaFieldFactory : PoetesseJavaScope

fun JavaFieldFactory.field(name: String, type: XTypeName<*, *>, block: JavaFieldScope.() -> Unit = {}) =
    JavaFieldRef(name) { JavaFieldScope.of(settings, name, type).apply(block).build() }

fun JavaFieldFactory.field(type: XTypeName<*, *>, block: JavaFieldScope.() -> Unit = {}) =
    LazyDelegate { name -> field(name, type, block) }

fun JavaFieldFactory.field(
    name: String, type: KClass<*>, nullable: Boolean = false, block: JavaFieldScope.() -> Unit = {}
) = field(name, xType(type, nullable = nullable), block)

fun JavaFieldFactory.field(type: KClass<*>, nullable: Boolean = false, block: JavaFieldScope.() -> Unit = {}) =
    LazyDelegate { name -> field(name, type, nullable, block) }

inline fun <reified T> JavaFieldFactory.field(
    name: String, nullable: Boolean = true, noinline block: JavaFieldScope.() -> Unit = {}
) = field(name, T::class, nullable, block)

inline fun <reified T : Any> JavaFieldFactory.field(name: String, noinline block: JavaFieldScope.() -> Unit = {}) =
    field<T>(name, nullable = false, block)

inline fun <reified T> JavaFieldFactory.field(
    nullable: Boolean = true, noinline block: JavaFieldScope.() -> Unit = {}
) = LazyDelegate { name -> field<T>(name, nullable, block) }

inline fun <reified T : Any> JavaFieldFactory.field(noinline block: JavaFieldScope.() -> Unit = {}) =
    field<T>(nullable = false, block)
