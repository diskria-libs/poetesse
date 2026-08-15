package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement
import io.github.diskria.poetesse.interop.PoetesseXScope

class KotlinConstructorScope private constructor(
    override val config: Poetesse.Config,
    private val specBuilder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinParameterContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility,
    KotlinBodyContainer {

    internal typealias Block = KotlinConstructorScope.() -> Unit

    internal val parameterContainer = KotlinParameterContainerInternal { specBuilder.addParameter(it) }
    internal val annotationContainer = KotlinAnnotationContainerInternal { specBuilder.addAnnotation(it) }
    internal val modifierContainer = KotlinModifierContainerInternal { specBuilder.addModifiers(it) }
    internal val statementContainer = KotlinBodyContainerInternal { specBuilder.addStatement(it) }

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)

    internal fun build() = specBuilder.build()

    internal companion object {
        context(scope: PoetesseXScope)
        fun of() = KotlinConstructorScope(scope.config, KPFunction.constructorBuilder())
    }
}
