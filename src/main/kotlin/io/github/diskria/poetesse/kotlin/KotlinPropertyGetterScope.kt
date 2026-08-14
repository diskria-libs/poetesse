package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement

class KotlinPropertyGetterScope private constructor(
    override val settings: Poetesse.Settings,
    internal val specBuilder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility,
    KotlinBodyContainer {

    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )
    internal val bodyContainer = KotlinBodyContainerInternal.of(
        append = { specBuilder.addStatement(it) },
    )

    fun expect() {
        modifiers(KPModifier.EXPECT)
    }

    fun actual() {
        modifiers(KPModifier.ACTUAL)
    }

    fun inline() {
        modifiers(KPModifier.INLINE)
    }

    internal fun build(): KPFunction =
        specBuilder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings): KotlinPropertyGetterScope =
            KotlinPropertyGetterScope(settings, KPFunction.getterBuilder())
    }
}
