package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.extensions.addStatement

@PoetesseKotlin
class KotlinConstructorScope private constructor(
    private val specBuilder: KPFunctionBuilder
) : KotlinParameterContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility {

    internal val parameterContainer = KotlinParameterContainerInternal.of(
        append = { specBuilder.addParameter(it) }
    )
    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun expect() {
        modifiers(KPModifier.EXPECT)
    }

    fun actual() {
        modifiers(KPModifier.ACTUAL)
    }

    fun final() {
        modifiers(KPModifier.FINAL)
    }

    fun open() {
        modifiers(KPModifier.OPEN)
    }

    fun abstract() {
        modifiers(KPModifier.ABSTRACT)
    }

    fun external() {
        modifiers(KPModifier.EXTERNAL)
    }

    fun body(block: BodyScope.() -> Unit) {
        BodyScope().apply(block)
    }

    internal fun build(): KPFunction =
        specBuilder.build()

    inner class BodyScope : KotlinCodeBlockContainer {
        internal val codeBlockContainer = KotlinCodeBlockContainerInternal.of(
            append = { specBuilder.addStatement(it) }
        )
    }

    internal companion object {
        fun of(): KotlinConstructorScope =
            KotlinConstructorScope(KPFunction.constructorBuilder())
    }
}
