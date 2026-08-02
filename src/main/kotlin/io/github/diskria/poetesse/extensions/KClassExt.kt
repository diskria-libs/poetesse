package io.github.diskria.poetesse.extensions

import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.kotlin.KPClassName
import kotlin.reflect.KClass

internal fun KClass<*>.asKPClassName(): KPClassName = asClassName()

internal fun KClass<*>.asJPClassName(): JPClassName = JPClassName.get(java)
