package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava

@PoetesseJava
class JavaMethodScope private constructor(
    private val specBuilder: JPMethodBuilder
) : JavaModifierContainer,
    JavaAnnotationContainer {

    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )
    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )

    fun body(block: Body.() -> Unit) {
        Body().apply(block)
    }

    fun static() {
        modifiers(JPModifier.STATIC)
    }

    internal fun build(): JPMethod =
        specBuilder.build()

    inner class Body : JavaCodeBlockContainer {
        internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
            append = { specBuilder.addStatement(it) }
        )
    }

    internal companion object {
        fun of(name: String): JavaMethodScope =
            JavaMethodScope(JPMethod.methodBuilder(name))
    }
}
