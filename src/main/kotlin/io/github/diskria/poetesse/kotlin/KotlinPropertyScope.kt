package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

@PoetesseKotlin
class KotlinPropertyScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: KPPropertyBuilder
) : KotlinTypeVariableContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility {

    internal val typeVariableContainer = KotlinTypeVariableContainerInternal.of(
        append = { specBuilder.addTypeVariable(it) }
    )
    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun expect() {
        modifiers(KPModifier.EXPECT)
    }

    fun actual() {
        modifiers(KPModifier.ACTUAL)
    }

    fun final() {
        modifiers(KPModifier.FINAL)
    }

    fun open() {
        modifiers(KPModifier.OPEN)
    }

    fun abstract() {
        modifiers(KPModifier.ABSTRACT)
    }

    fun const() {
        modifiers(KPModifier.CONST)
    }

    fun external() {
        modifiers(KPModifier.EXTERNAL)
    }

    fun override() {
        modifiers(KPModifier.OVERRIDE)
    }

    fun lateinit() {
        modifiers(KPModifier.LATEINIT)
    }

    internal fun build(): KPProperty =
        specBuilder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, name: String, type: XTypeName<*, *>): KotlinPropertyScope =
            KotlinPropertyScope(settings, KPProperty.builder(name, type.interopToKotlin()))
    }
}
