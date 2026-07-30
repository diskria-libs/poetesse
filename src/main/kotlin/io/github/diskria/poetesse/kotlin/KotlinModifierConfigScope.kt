package io.github.diskria.poetesse.kotlin

class KotlinModifierConfigScope private constructor() {

    sealed interface External {

        fun modifiers(vararg modifiers: KPModifier) {
            internal.append(*modifiers)
        }

        fun visibility(visibility: KotlinVisibility) {
            modifiers(
                when (visibility) {
                    KotlinVisibility.PUBLIC -> KPModifier.PUBLIC
                    KotlinVisibility.PROTECTED -> KPModifier.PROTECTED
                    KotlinVisibility.INTERNAL -> KPModifier.INTERNAL
                    KotlinVisibility.PRIVATE -> KPModifier.PRIVATE
                }
            )
        }

        fun public() {
            visibility(KotlinVisibility.PUBLIC)
        }

        fun protected() {
            visibility(KotlinVisibility.PROTECTED)
        }

        fun internal() {
            visibility(KotlinVisibility.INTERNAL)
        }

        fun private() {
            visibility(KotlinVisibility.PRIVATE)
        }

        fun expect() {
            modifiers(KPModifier.EXPECT)
        }

        fun actual() {
            modifiers(KPModifier.ACTUAL)
        }

        fun final() {
            modifiers(KPModifier.FINAL)
        }

        fun open() {
            modifiers(KPModifier.OPEN)
        }

        fun abstract() {
            modifiers(KPModifier.ABSTRACT)
        }

        fun external() {
            modifiers(KPModifier.EXTERNAL)
        }
    }

    internal interface Internal {

        fun append(vararg modifiers: KPModifier)

        companion object {
            fun of(
                append: (modifiers: Array<out KPModifier>) -> Unit,
            ): Internal = object : Internal {
                override fun append(vararg modifiers: KPModifier) = append(modifiers)
            }
        }
    }
}

private val KotlinModifierConfigScope.External.internal: KotlinModifierConfigScope.Internal
    get() = when (this) {
        is KotlinTypeScope -> modifierConfigInternalScope
        is KotlinFunctionScope -> modifierConfigInternalScope
    }
