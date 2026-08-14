package io.github.diskria.poetesse.kotlin

interface KotlinConstructorFactory : PoetesseKotlinScope

fun KotlinConstructorFactory.constructor(
    primary: Boolean = false,
    block: KotlinConstructorScope.() -> Unit = {}
) = KotlinConstructorRef(primary) { KotlinConstructorScope.of(settings).apply(block).build() }
