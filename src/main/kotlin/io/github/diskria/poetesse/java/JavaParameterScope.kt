package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

class JavaParameterScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: JPParameterBuilder,
) : PoetesseJavaScope,
    JavaAnnotationContainer,
    JavaModifierContainer {

    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun final() {
        modifiers(JPModifier.FINAL)
    }

    internal fun build(): JPParameter =
        specBuilder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, name: String, type: XTypeName<*, *>): JavaParameterScope =
            JavaParameterScope(settings, JPParameter.builder(type.interopToJava(), name))
    }
}
