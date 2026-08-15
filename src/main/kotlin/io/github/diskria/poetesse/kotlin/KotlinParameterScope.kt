package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

class KotlinParameterScope private constructor(
    override val config: Poetesse.Config,
    private val specBuilder: KPParameterBuilder,
) : PoetesseKotlinScope,
    KotlinAnnotationContainer,
    KotlinModifierContainer {

    internal typealias Block = KotlinParameterScope.() -> Unit

    internal val annotationContainer = KotlinAnnotationContainerInternal { specBuilder.addAnnotation(it) }
    internal val modifierContainer = KotlinModifierContainerInternal { specBuilder.addModifiers(it) }

    fun vararg() = modifier(KPModifier.VARARG)
    fun noinline() = modifier(KPModifier.NOINLINE)
    fun crossinline() = modifier(KPModifier.CROSSINLINE)

    internal fun build() = specBuilder.build()

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(name: String, type: XTypeName) =
            KotlinParameterScope(scope.config, KPParameter.builder(name, type.interopToKotlin()))
    }
}
