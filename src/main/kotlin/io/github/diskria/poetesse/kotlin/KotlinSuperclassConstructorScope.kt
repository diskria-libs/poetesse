package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope

open class KotlinSuperclassConstructorScope internal constructor(
    override val config: Poetesse.Config,
    private val builder: KPTypeBuilder,
) : KotlinArgumentTrait {

    internal typealias Block = KotlinSuperclassConstructorScope.() -> Unit

    internal val argumentsContainer by lazy { KotlinArgumentContainer(builder::addSuperclassConstructorParameter) }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(builder: KPTypeBuilder) = KotlinSuperclassConstructorScope(poetesse.config, builder)
    }
}
