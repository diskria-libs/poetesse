package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseScope

interface KotlinCodeFactory : PoetesseScope {

    fun code(build: KotlinCodeBuilder): KotlinCodeRef =
        KotlinCodeScope.of(settings, build)
}
