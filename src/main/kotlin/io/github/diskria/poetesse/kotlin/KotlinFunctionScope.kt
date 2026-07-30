package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseKotlin

@PoetesseKotlin
class KotlinFunctionScope private constructor(
    private val specBuilder: KPFunctionBuilder
) : KotlinModifierContainer {

    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun override() {
        modifiers(KPModifier.OVERRIDE)
    }

    fun tailrec() {
        modifiers(KPModifier.TAILREC)
    }

    fun suspend() {
        modifiers(KPModifier.SUSPEND)
    }

    fun inline() {
        modifiers(KPModifier.INLINE)
    }

    fun infix() {
        modifiers(KPModifier.INFIX)
    }

    fun operator() {
        modifiers(KPModifier.OPERATOR)
    }

    internal fun build(): KPFunction =
        specBuilder.build()

    internal companion object {
        fun of(name: String): KotlinFunctionScope =
            KotlinFunctionScope(KPFunction.builder(name))
    }
}
