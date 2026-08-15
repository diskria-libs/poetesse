package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope

class JavaConstructorScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPMethodBuilder,
) : PoetesseJavaScope,
    JavaParameterContainer,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility,
    JavaBodyContainer {

    internal typealias Block = JavaConstructorScope.() -> Unit

    internal val parameterContainer = JavaParameterContainerInternal { builder.addParameter(it) }
    internal val annotationContainer = JavaAnnotationContainerInternal { builder.addAnnotation(it) }
    internal val modifierContainer = JavaModifierContainerInternal { builder.addModifiers(it) }
    internal val statementContainer = JavaBodyContainerInternal { builder.addStatement(it) }

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = JavaConstructorScope(poetesse.config, JPMethod.constructorBuilder())
    }
}
