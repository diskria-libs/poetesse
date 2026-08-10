package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

@PoetesseKotlin
class KotlinParameterScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: KPParameterBuilder
) : KotlinAnnotationContainer,
    KotlinModifierContainer {

    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun vararg() {
        modifiers(KPModifier.VARARG)
    }

    fun noinline() {
        modifiers(KPModifier.NOINLINE)
    }

    fun crossinline() {
        modifiers(KPModifier.CROSSINLINE)
    }

    internal fun build(): KPParameter =
        specBuilder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, name: String, type: XTypeName<*, *>): KotlinParameterScope =
            KotlinParameterScope(settings, KPParameter.builder(name, type.interopToKotlin()))
    }
}
