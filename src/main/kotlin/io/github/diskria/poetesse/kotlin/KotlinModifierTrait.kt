package io.github.diskria.poetesse.kotlin

sealed interface KotlinModifierTrait : PoetesseKotlinScope {

    sealed interface WithVisibility : KotlinModifierTrait {
        fun public() = modifier(KPModifier.PUBLIC)
        fun protected() = modifier(KPModifier.PROTECTED)
        fun internal() = modifier(KPModifier.INTERNAL)
        fun private() = modifier(KPModifier.PRIVATE)
    }
}

fun KotlinModifierTrait.modifier(modifier: KPModifier) {
    internal.append(modifier)
}

internal class KotlinModifierContainerInternal(val append: (modifier: KPModifier) -> Unit)

private val KotlinModifierTrait.internal: KotlinModifierContainerInternal
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
