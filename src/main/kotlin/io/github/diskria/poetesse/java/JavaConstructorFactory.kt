package io.github.diskria.poetesse.java

interface JavaConstructorFactory : PoetesseJavaScope

fun JavaConstructorFactory.constructor(block: JavaConstructorScope.Block = {}) =
    JavaConstructorRef { JavaConstructorScope.of(settings).apply(block).build() }
