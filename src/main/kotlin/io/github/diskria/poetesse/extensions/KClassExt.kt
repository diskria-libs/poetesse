package io.github.diskria.poetesse.extensions

import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.kotlin.KPClassName
import kotlin.reflect.KClass

fun KClass<*>.asKPClassName(): KPClassName = asClassName()

fun KClass<*>.asJPClassName(): JPClassName = JPClassName.get(java)
