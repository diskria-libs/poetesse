package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.LazyDelegate

interface KotlinFunctionFactory : PoetesseKotlinScope

fun KotlinFunctionFactory.function(name: String, block: KotlinFunctionScope.Block = {}) =
    KotlinFunctionRef(name) { KotlinFunctionScope.of(name).apply(block).build() }

fun KotlinFunctionFactory.function(block: KotlinFunctionScope.Block = {}) =
    LazyDelegate { function(it, block) }
