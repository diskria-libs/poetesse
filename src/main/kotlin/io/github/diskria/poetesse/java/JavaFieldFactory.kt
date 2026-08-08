package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXTypeName
import io.github.diskria.poetesse.interop.setNullable
import kotlin.reflect.KClass

interface JavaFieldFactory

fun JavaFieldFactory.field(
    name: String,
    type: XTypeName,
    block: JavaFieldScope.() -> Unit = {}
) = JavaFieldRef(name) { JavaFieldScope.of(name, type).apply(block).build() }

fun JavaFieldFactory.field(
    type: XTypeName,
    block: JavaFieldScope.() -> Unit = {}
) = LazyDelegate { name -> field(name, type, block) }

fun JavaFieldFactory.field(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    block: JavaFieldScope.() -> Unit = {}
) = field(name, type.asXTypeName().setNullable(nullable), block)

fun JavaFieldFactory.field(
    type: KClass<out Any>,
    nullable: Boolean = false,
    block: JavaFieldScope.() -> Unit = {}
) = LazyDelegate { name -> field(name, type, nullable, block) }

inline fun <reified T : Any> JavaFieldFactory.field(
    name: String,
    nullable: Boolean = false,
    noinline block: JavaFieldScope.() -> Unit = {}
) = field(name, T::class, nullable, block)

inline fun <reified T : Any> JavaFieldFactory.field(
    nullable: Boolean = false,
    noinline block: JavaFieldScope.() -> Unit = {}
) = LazyDelegate { name -> field<T>(name, nullable, block) }
