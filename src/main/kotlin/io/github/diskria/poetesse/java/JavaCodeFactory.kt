package io.github.diskria.poetesse.java

interface JavaCodeFactory {

    fun code(buildCode: JavaCodeBuilder): JavaCodeRef =
        JavaCodeScope.of(buildCode)
}
