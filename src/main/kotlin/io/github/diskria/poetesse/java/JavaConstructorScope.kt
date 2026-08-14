package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse

class JavaConstructorScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: JPMethodBuilder,
) : PoetesseJavaScope,
    JavaParameterContainer,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility,
    JavaBodyContainer {

    internal typealias Block = JavaConstructorScope.() -> Unit

    internal val parameterContainer = JavaParameterContainerInternal.of(
        append = { specBuilder.addParameter(it) }
    )
    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )
    internal val statementContainer = JavaBodyContainerInternal.of(
        append = { specBuilder.addStatement(it) }
    )

    internal fun build(): JPMethod =
        specBuilder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings) = JavaConstructorScope(settings, JPMethod.constructorBuilder())
    }
}
