package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

class JavaParameterScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPParameterBuilder,
) : PoetesseJavaScope,
    JavaAnnotationContainer,
    JavaModifierContainer {

    internal typealias Block = JavaParameterScope.() -> Unit

    internal val annotationContainer = JavaAnnotationContainerInternal { builder.addAnnotation(it) }
    internal val modifierContainer = JavaModifierContainerInternal { builder.addModifiers(it) }

    fun final() = modifier(JPModifier.FINAL)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, type: XTypeName) =
            JavaParameterScope(poetesse.config, JPParameter.builder(type.interopToJava(), name))
    }
}
