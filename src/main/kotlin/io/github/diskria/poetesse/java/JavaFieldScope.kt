package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

@PoetesseJava
class JavaFieldScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: JPFieldBuilder
) : JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility {

    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun initializer(block: JavaCodeBuilder) {
        specBuilder.initializer(JavaCodeScope.of(settings, block).codeBlock)
    }

    internal fun build(): JPField =
        specBuilder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, name: String, type: XTypeName<*, *>): JavaFieldScope =
            JavaFieldScope(settings, JPField.builder(type.interopToJava(), name))
    }
}
