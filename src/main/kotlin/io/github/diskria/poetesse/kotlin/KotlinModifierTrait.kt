package io.github.diskria.poetesse.kotlin

sealed interface KotlinModifierTrait : PoetesseKotlinScope {
    operator fun KPModifier.unaryPlus() {
        container.append(this)
    }

    sealed interface WithVisibility : KotlinModifierTrait {
        fun public() = modifier(KPModifier.PUBLIC)
        fun protected() = modifier(KPModifier.PROTECTED)
        fun internal() = modifier(KPModifier.INTERNAL)
        fun private() = modifier(KPModifier.PRIVATE)
    }
}

fun KotlinModifierTrait.modifier(modifier: KPModifier) {
    +modifier
}

internal class KotlinModifierContainer(val append: (modifier: KPModifier) -> Unit)

private val KotlinModifierTrait.container: KotlinModifierContainer
    get() = when (this) {
        is AbstractKotlinTypeScope -> modifierContainer
        is KotlinCompanionObjectTypeScope -> modifierContainer
        is KotlinTypeAliasScope -> modifierContainer
        is KotlinPropertyScope -> modifierContainer
        is KotlinPropertyGetterScope -> modifierContainer
        is KotlinPropertySetterScope -> modifierContainer
        is KotlinConstructorScope -> modifierContainer
        is KotlinFunctionScope -> modifierContainer
        is KotlinParameterScope -> modifierContainer
        is KotlinVariableScope -> modifierContainer
    }
