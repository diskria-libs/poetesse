package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseScope

interface JavaConstructorFactory : PoetesseScope

fun JavaConstructorFactory.constructor(block: JavaConstructorScope.() -> Unit = {}): JavaConstructorRef =
    JavaConstructorRef { JavaConstructorScope.of(settings).apply(block).build() }
