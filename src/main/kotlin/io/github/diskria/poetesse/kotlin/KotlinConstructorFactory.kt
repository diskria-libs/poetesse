package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseScope

interface KotlinConstructorFactory : PoetesseScope

fun KotlinConstructorFactory.constructor(
    primary: Boolean = false,
    block: KotlinConstructorScope.() -> Unit = {}
) = KotlinConstructorRef(primary) { KotlinConstructorScope.of(settings).apply(block).build() }
