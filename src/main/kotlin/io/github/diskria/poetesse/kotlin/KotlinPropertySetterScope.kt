package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement
import io.github.diskria.poetesse.interop.PoetesseXScope

class KotlinPropertySetterScope private constructor(
    override val config: Poetesse.Config,
    internal val specBuilder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinParameterContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility,
    KotlinBodyContainer {

    internal typealias Block = KotlinPropertySetterScope.() -> Unit

    internal val parameterContainer = KotlinParameterContainerInternal.of(
        append = { specBuilder.addParameter(it) }
    )
    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(it) }
    )
    internal val statementContainer = KotlinBodyContainerInternal.of(
        append = { specBuilder.addStatement(it) },
    )

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)
    fun inline() = modifier(KPModifier.INLINE)

    internal fun build() = specBuilder.build()

    internal companion object {
        context(scope: PoetesseXScope)
        fun of() = KotlinPropertySetterScope(scope.config, KPFunction.setterBuilder())
    }
}
