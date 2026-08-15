package io.github.diskria.poetesse.kotlin

interface KotlinCodeFactory : PoetesseKotlinScope {

    fun code(block: KotlinCodeScope.Block): KotlinCodeRef =
        KotlinCodeScope.of(block)
}
