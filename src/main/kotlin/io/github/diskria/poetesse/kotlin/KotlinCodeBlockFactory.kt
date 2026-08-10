package io.github.diskria.poetesse.kotlin

interface KotlinCodeBlockFactory : KotlinCodeFactory {

    fun codeBlock(build: KotlinCodeBlockScope.() -> Unit): KotlinCodeBlockRef =
        KotlinCodeBlockRef { KotlinCodeBlockScope(settings).apply(build).build() }
}
