package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope

class JavaCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    internal val isComment: Boolean,
    private val builder: JPCodeBlockBuilder = JPCodeBlock.builder(),
) : PoetesseJavaScope,
    JavaCodeBlockTrait {

    internal typealias Block = JavaCodeBlockScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainer {
        if (isComment) {
            builder.add("$[")
            builder.add(it)
            builder.add("\n$]")
        } else {
            builder.addStatement(it)
        }
    }

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(isComment: Boolean = false) = JavaCodeBlockScope(poetesse.config, isComment)
    }
}

class JavaEmbeddableCodeBlockScope private constructor(
    override val config: Poetesse.Config,
    internal val statements: MutableList<JPCodeBlock> = mutableListOf()
) : PoetesseJavaScope,
    JavaCodeBlockTrait {

    internal typealias Block = JavaEmbeddableCodeBlockScope.() -> Unit

    internal val codeBlockContainer = JavaCodeBlockContainer { statements += it }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = JavaEmbeddableCodeBlockScope(poetesse.config)
    }
}
