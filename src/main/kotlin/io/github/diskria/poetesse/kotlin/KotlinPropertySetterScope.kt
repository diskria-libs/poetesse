package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement
import io.github.diskria.poetesse.interop.PoetesseScope

class KotlinPropertySetterScope private constructor(
    override val config: Poetesse.Config,
    internal val builder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinParameterContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility,
    KotlinBodyContainer {

    internal typealias Block = KotlinPropertySetterScope.() -> Unit

    internal val parameterContainer = KotlinParameterContainerInternal { builder.addParameter(it) }
    internal val annotationContainer = KotlinAnnotationContainerInternal { builder.addAnnotation(it) }
    internal val modifierContainer = KotlinModifierContainerInternal { builder.addModifiers(it) }
    internal val statementContainer = KotlinBodyContainerInternal { builder.addStatement(it) }

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)
    fun inline() = modifier(KPModifier.INLINE)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = KotlinPropertySetterScope(poetesse.config, KPFunction.setterBuilder())
    }
}
