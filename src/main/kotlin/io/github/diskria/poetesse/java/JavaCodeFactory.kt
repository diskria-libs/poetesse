package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseScope

interface JavaCodeFactory : PoetesseScope {

    fun code(build: JavaCodeBuilder): JavaCodeRef =
        JavaCodeScope.of(settings, build)
}
