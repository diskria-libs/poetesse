package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

class KotlinTypeAliasScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: KPTypeAliasBuilder,
) : PoetesseKotlinScope,
    KotlinTypeVariableContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility {

    internal typealias Block = KotlinTypeAliasScope.() -> Unit

    internal val typeVariableContainer = KotlinTypeVariableContainerInternal.of(
        append = { specBuilder.addTypeVariable(it) }
    )
    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    internal fun build(): KPTypeAlias =
        specBuilder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, name: String, type: XTypeName) =
            KotlinTypeAliasScope(settings, KPTypeAlias.builder(name, type.interopToKotlin()))
    }
}
