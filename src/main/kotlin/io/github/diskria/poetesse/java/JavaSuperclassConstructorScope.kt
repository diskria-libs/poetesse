package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope

open class JavaSuperclassConstructorScope internal constructor(
    override val config: Poetesse.Config,
) : JavaArgumentTrait {

    internal typealias Block = JavaSuperclassConstructorScope.() -> Unit

    internal val argumentContainer by lazy { JavaArgumentContainer { arguments += it } }

    private val arguments: MutableList<JPCodeBlock> = mutableListOf()

    fun joinArguments(): JPCodeBlock = JPCodeBlock.join(arguments, ", ")

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = JavaSuperclassConstructorScope(poetesse.config)
    }
}
