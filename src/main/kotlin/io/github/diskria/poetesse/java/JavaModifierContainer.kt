package io.github.diskria.poetesse.java

sealed interface JavaModifierContainer {

    fun modifiers(vararg modifiers: JPModifier) {
        internal.append(*modifiers)
    }

    sealed interface WithVisibility : JavaModifierContainer {

        fun visibility(visibility: JavaVisibility) {
            if (visibility == JavaVisibility.PACKAGE_PRIVATE) return
            modifiers(
                when (visibility) {
                    JavaVisibility.PUBLIC -> JPModifier.PUBLIC
                    JavaVisibility.PROTECTED -> JPModifier.PROTECTED
                    JavaVisibility.PRIVATE -> JPModifier.PRIVATE
                }
            )
        }

        fun public() {
            visibility(JavaVisibility.PUBLIC)
        }

        fun protected() {
            visibility(JavaVisibility.PROTECTED)
        }

        fun packagePrivate() {
            visibility(JavaVisibility.PACKAGE_PRIVATE)
        }

        fun private() {
            visibility(JavaVisibility.PRIVATE)
        }
    }
}

internal interface JavaModifierContainerInternal {

    fun append(vararg modifiers: JPModifier)

    companion object {
        fun of(
            append: (modifiers: Array<out JPModifier>) -> Unit,
        ): JavaModifierContainerInternal = object : JavaModifierContainerInternal {
            override fun append(vararg modifiers: JPModifier) = append(modifiers)
        }
    }
}

private val JavaModifierContainer.internal: JavaModifierContainerInternal
    get() = when (this) {
        is JavaTypeScope -> modifierContainer
        is JavaFieldScope -> modifierContainer
        is JavaConstructorScope -> modifierContainer
        is JavaMethodScope -> modifierContainer
        is JavaParameterScope -> modifierContainer
        is JavaVariableScope -> modifierContainer
    }
