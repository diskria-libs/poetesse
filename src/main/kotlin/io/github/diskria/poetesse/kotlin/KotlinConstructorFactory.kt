package io.github.diskria.poetesse.kotlin

interface KotlinConstructorFactory : PoetesseKotlinScope

fun KotlinConstructorFactory.constructor(primary: Boolean = true, block: KotlinConstructorScope.Block = {}) =
    KotlinConstructorRef(primary) { KotlinConstructorScope.of(it).apply(block).build() }
