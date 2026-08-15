package io.github.diskria.poetesse.java

sealed interface JavaModifierContainer : PoetesseJavaScope {
    sealed interface WithVisibility : JavaModifierContainer {
        fun public() = modifier(JPModifier.PUBLIC)
        fun protected() = modifier(JPModifier.PROTECTED)
        fun packagePrivate() {}
        fun private() = modifier(JPModifier.PRIVATE)
    }
}

fun JavaModifierContainer.modifier(modifier: JPModifier) {
    internal.append(modifier)
}

internal interface JavaModifierContainerInternal {

    fun append(modifier: JPModifier)

    companion object {
        fun of(
            append: (modifier: JPModifier) -> Unit,
        ): JavaModifierContainerInternal = object : JavaModifierContainerInternal {
            override fun append(modifier: JPModifier) = append(modifier)
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
