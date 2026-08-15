package io.github.diskria.poetesse.java

interface JavaCodeFactory : PoetesseJavaScope

fun JavaCodeFactory.code(block: JavaCodeScope.Block): JavaCodeRef =
    JavaCodeScope.of(block)
