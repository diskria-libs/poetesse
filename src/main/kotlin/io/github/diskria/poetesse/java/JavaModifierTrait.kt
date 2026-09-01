package io.github.diskria.poetesse.java

sealed interface JavaModifierTrait : PoetesseJavaScope {
    operator fun JPModifier.unaryPlus() {
        container.append(this)
    }

    sealed interface WithVisibility : JavaModifierTrait {
        fun public() = modifier(JPModifier.PUBLIC)
        fun protected() = modifier(JPModifier.PROTECTED)
        fun packagePrivate() {}
        fun private() = modifier(JPModifier.PRIVATE)
    }
}

fun JavaModifierTrait.modifier(modifier: JPModifier) {
    +modifier
}

internal class JavaModifierContainer(val append: (modifier: JPModifier) -> Unit)

private val JavaModifierTrait.container: JavaModifierContainer
    get() = when (this) {
        is AbstractJavaTypeScope -> modifierContainer
        is JavaFieldScope -> modifierContainer
        is JavaConstructorScope -> modifierContainer
        is JavaMethodScope -> modifierContainer
        is JavaParameterScope -> modifierContainer
        is JavaVariableScope -> modifierContainer
    }
