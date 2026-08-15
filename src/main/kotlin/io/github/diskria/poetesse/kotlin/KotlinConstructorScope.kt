package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XParameter

class KotlinConstructorScope private constructor(
    override val config: Poetesse.Config,
    private val outerBuilder: KPTypeBuilder,
    private val builder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinParameterContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility,
    KotlinBodyContainer {

    internal typealias Block = KotlinConstructorScope.() -> Unit

    internal val parameterContainer = KotlinParameterContainerInternal { builder.addParameter(it) }
    internal val annotationContainer = KotlinAnnotationContainerInternal { builder.addAnnotation(it) }
    internal val modifierContainer = KotlinModifierContainerInternal { builder.addModifiers(it) }
    internal val statementContainer = KotlinBodyContainerInternal { builder.addStatement(it) }

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
