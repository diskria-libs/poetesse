package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope

class JavaConstructorScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPMethodBuilder,
) : PoetesseJavaScope,
    JavaParameterTrait,
    JavaAnnotationTrait,
    JavaModifierTrait.WithVisibility,
    JavaBodyTrait {

    internal typealias Block = JavaConstructorScope.() -> Unit

    internal val parameterContainer = JavaParameterContainer(builder::addParameter)
    internal val annotationContainer = JavaAnnotationContainer(builder::addAnnotation)
    internal val modifierContainer = JavaModifierContainerInternal(builder::addModifiers)
    internal val statementContainer = JavaBodyContainerInternal(builder::addStatement)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = JavaConstructorScope(poetesse.config, JPMethod.constructorBuilder())
    }
}
