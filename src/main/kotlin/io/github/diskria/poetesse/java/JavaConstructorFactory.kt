package io.github.diskria.poetesse.java

interface JavaConstructorFactory

fun JavaConstructorFactory.constructor(block: JavaConstructorScope.() -> Unit = {}): JavaConstructorRef =
    JavaConstructorRef { JavaConstructorScope.of().apply(block).build() }
