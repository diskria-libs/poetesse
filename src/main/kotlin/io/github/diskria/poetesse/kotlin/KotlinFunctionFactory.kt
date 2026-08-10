package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.PoetesseScope

interface KotlinFunctionFactory : PoetesseScope

fun KotlinFunctionFactory.function(name: String, block: KotlinFunctionScope.() -> Unit = {}): KotlinFunctionRef =
    KotlinFunctionRef(name) { KotlinFunctionScope.of(settings, name).apply(block).build() }

fun KotlinFunctionFactory.function(block: KotlinFunctionScope.() -> Unit = {}): LazyDelegate<KotlinFunctionRef> =
    LazyDelegate { name -> function(name, block) }
