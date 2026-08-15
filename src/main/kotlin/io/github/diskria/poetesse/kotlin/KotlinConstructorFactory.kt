package io.github.diskria.poetesse.kotlin

interface KotlinConstructorFactory : PoetesseKotlinScope

fun KotlinConstructorFactory.constructor(primary: Boolean = false, block: KotlinConstructorScope.Block = {}) =
    KotlinConstructorRef(primary) { KotlinConstructorScope.of().apply(block).build() }
