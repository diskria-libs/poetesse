package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava

typealias JavaCodeBuilder = JavaCodeScope.() -> String

@PoetesseJava
class JavaCodeScope internal constructor(private val block: JavaCodeBuilder) : JavaCodeArgumentsScope() {

    internal fun build(): JPCodeBlock = build { block() }

    internal companion object {
        fun of(block: JavaCodeBuilder): JavaCodeRef =
            JavaCodeRef { JavaCodeScope(block).build() }
    }
}
