package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

class JavaParameterScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPParameterBuilder,
) : PoetesseJavaScope,
    JavaDocumentationTrait,
    JavaAnnotationTrait,
    JavaModifierTrait {

    internal typealias Block = JavaParameterScope.() -> Unit

    internal val documentationContainer by lazy { JavaDocumentationContainer(builder::addJavadoc) }
    internal val annotationContainer by lazy { JavaAnnotationContainer(builder::addAnnotation) }
    internal val modifierContainer by lazy { JavaModifierContainer(builder::addModifiers) }

    fun final() = modifier(JPModifier.FINAL)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, type: XTypeName) =
            JavaParameterScope(poetesse.config, JPParameter.builder(type.interopToJava(), name))
    }
}
