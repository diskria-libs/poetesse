package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XTypeName

@PoetesseJava
class JavaParameterScope private constructor(
    private val specBuilder: JPParameterBuilder
) : JavaAnnotationContainer,
    JavaFinalOnlyModifierContainer {

    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    internal fun build(): JPParameter =
        specBuilder.build()

    internal companion object {
        fun of(name: String, type: XTypeName): JavaParameterScope =
            JavaParameterScope(JPParameter.builder(type.interopToJava(), name))
    }
}
