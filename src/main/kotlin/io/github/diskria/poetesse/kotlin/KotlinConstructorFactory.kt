package io.github.diskria.poetesse.kotlin

interface KotlinConstructorFactory

fun KotlinConstructorFactory.constructor(
    primary: Boolean = false,
    block: KotlinConstructorScope.() -> Unit = {}
) = KotlinConstructorRef(primary) { KotlinConstructorScope.of().apply(block).build() }
