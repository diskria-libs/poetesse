package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import io.github.diskria.poetesse.interop.setNullable
import kotlin.reflect.KClass

interface JavaParameterFactory

fun JavaParameterFactory.parameter(
    name: String,
    type: XTypeName,
    block: JavaParameterScope.() -> Unit
) = JavaParameterRef(name) { JavaParameterScope.of(name, type).apply(block).build() }

fun JavaParameterFactory.parameter(
    type: XTypeName,
    block: JavaParameterScope.() -> Unit = {}
) = LazyDelegate { name -> parameter(name, type, block) }

fun JavaParameterFactory.parameter(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    block: JavaParameterScope.() -> Unit = {}
) = parameter(name, type.xType().setNullable(nullable), block)

fun JavaParameterFactory.parameter(
    type: KClass<out Any>,
    nullable: Boolean = false,
    block: JavaParameterScope.() -> Unit = {}
) = LazyDelegate { name -> parameter(name, type, nullable, block) }

inline fun <reified T : Any> JavaParameterFactory.parameter(
    name: String,
    nullable: Boolean = false,
    noinline block: JavaParameterScope.() -> Unit = {}
) = parameter(name, T::class, nullable, block)

inline fun <reified T : Any> JavaParameterFactory.parameter(
    nullable: Boolean = false,
    noinline block: JavaParameterScope.() -> Unit = {}
) = LazyDelegate { name -> parameter<T>(name, nullable, block) }
