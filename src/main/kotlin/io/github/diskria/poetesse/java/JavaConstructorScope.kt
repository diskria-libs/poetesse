package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.applyCodeBlockMutation
import io.github.diskria.poetesse.interop.PoetesseScope

class JavaConstructorScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPMethodBuilder,
) : PoetesseJavaScope,
    JavaDocumentationTrait,
    JavaAnnotationTrait,
    JavaModifierTrait.WithVisibility,
    JavaParameterTrait,
    JavaBodyTrait {

    internal typealias Block = JavaConstructorScope.() -> Unit

    internal val documentationContainer by lazy { JavaDocumentationContainer(builder::addJavadoc) }
    internal val annotationContainer by lazy { JavaAnnotationContainer(builder::addAnnotation) }
    internal val modifierContainer by lazy { JavaModifierContainer(builder::addModifiers) }
    internal val parameterContainer by lazy { JavaParameterContainer(builder::addParameter) }
    internal val statementContainer by lazy { JavaBodyContainer(builder::applyCodeBlockMutation) }

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = JavaConstructorScope(poetesse.config, JPMethod.constructorBuilder())
    }
}
