package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava

@PoetesseJava
class JavaConstructorScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: JPMethodBuilder
) : JavaParameterContainer,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility {

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
        BodyScope(settings).apply(block)
    }

    internal fun build(): JPMethod =
        specBuilder.build()

    @PoetesseJava
    inner class BodyScope(override val settings: Poetesse.Settings) : JavaCodeBlockContainer {
        internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
            append = { this@JavaConstructorScope.specBuilder.addStatement(it) }
        )
    }

    internal companion object {
        fun of(settings: Poetesse.Settings): JavaConstructorScope =
            JavaConstructorScope(settings, JPMethod.constructorBuilder())
    }
}
