package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.appendCommand
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XParameter

class KotlinConstructorScope private constructor(
    override val config: Poetesse.Config,
    private val outerBuilder: KPTypeBuilder,
    private val builder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinDocumentationTrait,
    KotlinAnnotationTrait,
    KotlinModifierTrait.WithVisibility,
    KotlinParameterTrait,
    KotlinBodyTrait {

    internal typealias Block = KotlinConstructorScope.() -> Unit

    internal val documentationContainer = KotlinDocumentationContainer(builder::addKdoc)
    internal val annotationContainer = KotlinAnnotationContainer(builder::addAnnotation)
    internal val modifierContainer = KotlinModifierContainer(builder::addModifiers)
    internal val parameterContainer = KotlinParameterContainer(builder::addParameter)
    internal val statementContainer = KotlinBodyContainer(builder::appendCommand)

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)

    fun XParameter.property(block: KotlinPropertyScope.Block = {}) {
        outerBuilder.addProperty(KotlinPropertyScope.of(name, type).apply {
            initializer { name }
            block()
        }.build())
    }

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(outerBuilder: KPTypeBuilder) =
            KotlinConstructorScope(poetesse.config, outerBuilder, KPFunction.constructorBuilder())
    }
}
