package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement
import io.github.diskria.poetesse.interop.PoetesseXScope

class KotlinPropertyGetterScope private constructor(
    override val config: Poetesse.Config,
    internal val specBuilder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility,
    KotlinBodyContainer {

    internal typealias Block = KotlinPropertyGetterScope.() -> Unit

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
        fun of() = KotlinPropertyGetterScope(scope.config, KPFunction.getterBuilder())
    }
}
