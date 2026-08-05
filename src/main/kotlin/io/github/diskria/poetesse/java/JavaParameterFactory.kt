package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXTypeName
import io.github.diskria.poetesse.interop.setNullable
import kotlin.reflect.KClass

interface JavaParameterFactory

fun JavaParameterFactory.parameter(
    name: String,
    type: XTypeName,
    interop: Boolean = true,
    block: JavaParameterScope.() -> Unit
) = JavaParameterRef(name) { JavaParameterScope.of(name, type, interop).apply(block).build() }

fun JavaParameterFactory.parameter(
    type: XTypeName,
    interop: Boolean = true,
    block: JavaParameterScope.() -> Unit = {}
) = LazyDelegate { name -> parameter(name, type, interop, block) }

fun JavaParameterFactory.parameter(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    block: JavaParameterScope.() -> Unit = {}
) = parameter(name, type.asXTypeName().setNullable(nullable), interop, block)

fun JavaParameterFactory.parameter(
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    block: JavaParameterScope.() -> Unit = {}
) = LazyDelegate { name -> parameter(name, type, nullable, interop, block) }

inline fun <reified T : Any> JavaParameterFactory.parameter(
    name: String,
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline block: JavaParameterScope.() -> Unit = {}
) = parameter(name, T::class, nullable, interop, block)

inline fun <reified T : Any> JavaParameterFactory.parameter(
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline block: JavaParameterScope.() -> Unit = {}
) = LazyDelegate { name -> parameter<T>(name, nullable, interop, block) }
