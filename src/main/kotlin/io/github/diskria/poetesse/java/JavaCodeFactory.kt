package io.github.diskria.poetesse.java

interface JavaCodeFactory {
    fun code(buildCode: JavaCodeBuilder): JavaDeferredCode =
        JavaCodeScope.of(buildCode)
}
