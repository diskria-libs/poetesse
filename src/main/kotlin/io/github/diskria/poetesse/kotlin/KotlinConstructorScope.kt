package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement

class KotlinConstructorScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinParameterContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility,
    KotlinBodyContainer {

    internal typealias Block = KotlinConstructorScope.() -> Unit

    internal val parameterContainer = KotlinParameterContainerInternal.of(
        append = { specBuilder.addParameter(it) }
    )
    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )
    internal val statementContainer = KotlinBodyContainerInternal.of(
        append = { specBuilder.addStatement(it) },
    )

    fun expect() {
        modifiers(KPModifier.EXPECT)
    }

    fun actual() {
        modifiers(KPModifier.ACTUAL)
    }

    internal fun build(): KPFunction =
        specBuilder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings) = KotlinConstructorScope(settings, KPFunction.constructorBuilder())
    }
}
