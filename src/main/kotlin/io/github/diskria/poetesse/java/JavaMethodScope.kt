package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava

@PoetesseJava
class JavaMethodScope private constructor(
    private val specBuilder: JPMethodBuilder
) : JavaAnnotationContainer,
    JavaVisibilityAllowedModifierContainer {

    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun body(block: BodyScope.() -> Unit) {
        BodyScope().apply(block)
    }

    fun static() {
        modifiers(JPModifier.STATIC)
    }

    internal fun build(): JPMethod =
        specBuilder.build()

    inner class BodyScope : JavaCodeBlockContainer {
        internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
            append = { specBuilder.addStatement(it) }
        )
    }

    internal companion object {
        fun of(name: String): JavaMethodScope =
            JavaMethodScope(JPMethod.methodBuilder(name))
    }
}
