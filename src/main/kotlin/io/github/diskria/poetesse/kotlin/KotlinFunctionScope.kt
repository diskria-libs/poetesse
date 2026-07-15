package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.FunSpec
import io.github.diskria.poetesse.PoetesseKotlin

@PoetesseKotlin
class KotlinFunctionScope private constructor(
    private val specBuilder: FunSpec.Builder
) : KotlinModifierConfigScope.External {

    internal val modifierConfigInternalScope = KotlinModifierConfigScope.Internal.of(
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

    internal fun build(): FunSpec =
        specBuilder.build()

    internal companion object {
        fun of(name: String): KotlinFunctionScope =
            KotlinFunctionScope(FunSpec.builder(name))
    }
}
