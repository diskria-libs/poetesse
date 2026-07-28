package io.github.diskria.poetesse.java

interface JavaCodeFactory

fun JavaCodeFactory.code(buildStatement: JavaStatementScope.() -> String): JavaDeferredCode =
    JavaStatementScope.of(buildStatement)
