package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

class JavaFieldScope private constructor(
    override val config: Poetesse.Config,
    private val specBuilder: JPFieldBuilder,
) : PoetesseJavaScope,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility {

    internal typealias Block = JavaFieldScope.() -> Unit

    internal val annotationContainer = JavaAnnotationContainerInternal { specBuilder.addAnnotation(it) }
    internal val modifierContainer = JavaModifierContainerInternal { specBuilder.addModifiers(it) }

    fun initializer(block: JavaCodeScope.Block) {
        specBuilder.initializer(JavaCodeScope.of(block).codeBlock)
    }

    internal fun build() = specBuilder.build()

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(name: String, type: XTypeName) =
            JavaFieldScope(scope.config, JPField.builder(type.interopToJava(), name))
    }
}
