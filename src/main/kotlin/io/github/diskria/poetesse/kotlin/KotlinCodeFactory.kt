package io.github.diskria.poetesse.kotlin

interface KotlinCodeFactory {

    fun code(build: KotlinCodeBuilder): KotlinCodeRef =
        KotlinCodeScope.of(build)
}
