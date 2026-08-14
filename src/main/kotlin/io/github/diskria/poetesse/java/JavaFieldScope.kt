package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

class JavaFieldScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: JPFieldBuilder,
) : PoetesseJavaScope,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility {

    internal typealias Block = JavaFieldScope.() -> Unit

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
        fun of(settings: Poetesse.Settings, name: String, type: XTypeName) =
            JavaFieldScope(settings, JPField.builder(type.interopToJava(), name))
    }
}
