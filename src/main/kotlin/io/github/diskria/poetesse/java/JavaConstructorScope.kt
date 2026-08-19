package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope

class JavaConstructorScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPMethodBuilder,
) : PoetesseJavaScope,
    JavaDocumentationTrait,
    JavaParameterTrait,
    JavaAnnotationTrait,
    JavaModifierTrait.WithVisibility,
    JavaBodyTrait {

    internal typealias Block = JavaConstructorScope.() -> Unit

    internal val documentationContainer = JavaDocumentationContainer(builder::addJavadoc)
    internal val parameterContainer = JavaParameterContainer(builder::addParameter)
    internal val annotationContainer = JavaAnnotationContainer(builder::addAnnotation)
    internal val modifierContainer = JavaModifierContainer(builder::addModifiers)
    internal val statementContainer = JavaBodyContainer(builder::addStatement)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = JavaConstructorScope(poetesse.config, JPMethod.constructorBuilder())
    }
}
