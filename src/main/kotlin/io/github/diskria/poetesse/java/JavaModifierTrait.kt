package io.github.diskria.poetesse.java

sealed interface JavaModifierTrait : PoetesseJavaScope {
    sealed interface WithVisibility : JavaModifierTrait {
        fun public() = modifier(JPModifier.PUBLIC)
        fun protected() = modifier(JPModifier.PROTECTED)
        fun packagePrivate() {}
        fun private() = modifier(JPModifier.PRIVATE)
    }
}

fun JavaModifierTrait.modifier(modifier: JPModifier) {
    internal.append(modifier)
}

internal class JavaModifierContainerInternal(val append: (modifier: JPModifier) -> Unit)

private val JavaModifierTrait.internal: JavaModifierContainerInternal
    get() = when (this) {
        is JavaTypeScope -> modifierContainer
        is JavaFieldScope -> modifierContainer
        is JavaConstructorScope -> modifierContainer
        is JavaMethodScope -> modifierContainer
        is JavaParameterScope -> modifierContainer
        is JavaVariableScope -> modifierContainer
    }
