package io.github.diskria.poetesse.java

interface JavaCodeFactory {

    fun code(build: JavaCodeBuilder): JavaCodeRef =
        JavaCodeScope.of(build)
}
