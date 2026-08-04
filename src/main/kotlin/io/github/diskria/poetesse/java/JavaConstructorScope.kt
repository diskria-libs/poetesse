package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava

@PoetesseJava
class JavaConstructorScope private constructor(
    private val specBuilder: JPMethodBuilder
) : JavaParameterContainer,
    JavaAnnotationContainer,
    JavaVisibilityOnlyModifierContainer {

    internal val parameterContainer = JavaParameterContainerInternal.of(
        append = { specBuilder.addParameter(it) }
    )
    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun body(block: BodyScope.() -> Unit) {
        BodyScope().apply(block)
    }

    internal fun build(): JPMethod =
        specBuilder.build()

    inner class BodyScope : JavaCodeBlockContainer {
        internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
            append = { specBuilder.addStatement(it) }
        )
    }

    internal companion object {
        fun of(): JavaConstructorScope =
            JavaConstructorScope(JPMethod.constructorBuilder())
    }
}
