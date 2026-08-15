package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

class JavaFieldScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPFieldBuilder,
) : PoetesseJavaScope,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility {

    internal typealias Block = JavaFieldScope.() -> Unit

    internal val annotationContainer = JavaAnnotationContainerInternal { builder.addAnnotation(it) }
    internal val modifierContainer = JavaModifierContainerInternal { builder.addModifiers(it) }

    fun initializer(block: JavaCodeScope.Block) {
        builder.initializer(JavaCodeScope.of(block).codeBlock)
    }

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, type: XTypeName) =
            JavaFieldScope(poetesse.config, JPField.builder(type.interopToJava(), name))
    }
}
