package io.github.diskria.poetesse.kotlin

interface KotlinCodeFactory : PoetesseKotlinScope {

    fun code(build: KotlinCodeBuilder): KotlinCodeRef =
        KotlinCodeScope.of(settings, build)
}
