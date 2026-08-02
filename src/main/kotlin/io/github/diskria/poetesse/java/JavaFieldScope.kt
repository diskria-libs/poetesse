package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XTypeName

@PoetesseJava
class JavaFieldScope private constructor(
    private val specBuilder: JPFieldBuilder
) : JavaAnnotationContainer,
    JavaVisibilityAllowedModifierContainer {

    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun initializer(block: JavaCodeBuilder) {
        specBuilder.initializer(JavaCodeScope.of(block).codeBlock)
    }

    internal fun build(): JPField =
        specBuilder.build()

    internal companion object {
        fun of(name: String, type: XTypeName, interop: Boolean): JavaFieldScope =
            JavaFieldScope(JPField.builder(type.toJava(interop), name))
    }
}
