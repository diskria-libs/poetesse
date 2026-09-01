package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.applyCodeBlockMutation
import io.github.diskria.poetesse.interop.PoetesseScope

class KotlinPropertySetterScope private constructor(
    override val config: Poetesse.Config,
    internal val builder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinDocumentationTrait,
    KotlinAnnotationTrait,
    KotlinModifierTrait.WithVisibility,
    KotlinParameterTrait,
    KotlinBodyTrait {

    internal typealias Block = KotlinPropertySetterScope.() -> Unit

    internal val documentationContainer by lazy { KotlinDocumentationContainer(builder::addKdoc) }
    internal val annotationContainer by lazy { KotlinAnnotationContainer(builder::addAnnotation) }
    internal val modifierContainer by lazy { KotlinModifierContainer(builder::addModifiers) }
    internal val parameterContainer by lazy { KotlinParameterContainer(builder::addParameter) }
    internal val statementContainer by lazy { KotlinBodyContainer(builder::applyCodeBlockMutation) }

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)
    fun inline() = modifier(KPModifier.INLINE)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = KotlinPropertySetterScope(poetesse.config, KPFunction.setterBuilder())
    }
}
