package io.github.diskria.poetesse.java

sealed interface JavaModifierContainer {

    fun modifiers(vararg modifiers: JPModifier) {
        internal.append(*modifiers)
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
