package io.github.diskria.poetesse.java

interface JavaCodeFactory

fun JavaCodeFactory.code(buildCode: JavaCodeBuilder): JavaDeferredCode =
    JavaCodeScope.of(buildCode)
