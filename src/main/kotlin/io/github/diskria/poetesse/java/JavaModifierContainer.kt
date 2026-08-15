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

internal class JavaModifierContainerInternal(val append: (modifier: JPModifier) -> Unit)

private val JavaModifierContainer.internal: JavaModifierContainerInternal
    get() = when (this) {
        is JavaTypeScope -> modifierContainer
        is JavaFieldScope -> modifierContainer
        is JavaConstructorScope -> modifierContainer
        is JavaMethodScope -> modifierContainer
        is JavaParameterScope -> modifierContainer
        is JavaVariableScope -> modifierContainer
    }
