package io.github.diskria.poetesse.kotlin

sealed interface KotlinModifierContainer : PoetesseKotlinScope {
    sealed interface WithVisibility : KotlinModifierContainer {
        fun public() = modifier(KPModifier.PUBLIC)
        fun protected() = modifier(KPModifier.PROTECTED)
        fun internal() = modifier(KPModifier.INTERNAL)
        fun private() = modifier(KPModifier.PRIVATE)
    }
}

fun KotlinModifierContainer.modifier(modifier: KPModifier) {
    internal.append(modifier)
}

internal class KotlinModifierContainerInternal(val append: (modifier: KPModifier) -> Unit)

private val KotlinModifierContainer.internal: KotlinModifierContainerInternal
    get() = when (this) {
        is KotlinTypeScope -> modifierContainer
        is KotlinTypeAliasScope -> modifierContainer
        is KotlinPropertyScope -> modifierContainer
        is KotlinPropertyGetterScope -> modifierContainer
        is KotlinPropertySetterScope -> modifierContainer
        is KotlinConstructorScope -> modifierContainer
        is KotlinFunctionScope -> modifierContainer
        is KotlinParameterScope -> modifierContainer
        is KotlinVariableScope -> modifierContainer
    }
