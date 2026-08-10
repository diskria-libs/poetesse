package io.github.diskria.poetesse.java

interface JavaCodeBlockFactory : JavaCodeFactory {

    fun codeBlock(build: JavaCodeBlockScope.() -> Unit): JavaCodeBlockRef =
        JavaCodeBlockRef { JavaCodeBlockScope(settings).apply(build).build() }
}
