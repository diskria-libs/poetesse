package io.github.diskria.poetesse.kotlin

sealed interface KotlinModifierContainer {

    fun modifiers(vararg modifiers: KPModifier) {
        internal.append(*modifiers)
    }

    sealed interface WithVisibility : KotlinModifierContainer {

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
    }
}

internal interface KotlinModifierContainerInternal {

    fun append(vararg modifiers: KPModifier)

    companion object {
        fun of(
            append: (modifiers: Array<out KPModifier>) -> Unit,
        ): KotlinModifierContainerInternal = object : KotlinModifierContainerInternal {
            override fun append(vararg modifiers: KPModifier) = append(modifiers)
        }
    }
}

private val KotlinModifierContainer.internal: KotlinModifierContainerInternal
    get() = when (this) {
        is KotlinTypeScope -> modifierContainer
        is KotlinPropertyScope -> modifierContainer
        is KotlinConstructorScope -> modifierContainer
        is KotlinFunctionScope -> modifierContainer
        is KotlinParameterScope -> modifierContainer
        is KotlinVariableScope -> modifierContainer
    }
