package io.github.diskria.poetesse.java

interface JavaCodeFactory : PoetesseJavaScope {

    fun code(build: JavaCodeBuilder): JavaCodeRef =
        JavaCodeScope.of(settings, build)
}
