package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

class KotlinTypeAliasScope private constructor(
    override val config: Poetesse.Config,
    private val specBuilder: KPTypeAliasBuilder,
) : PoetesseKotlinScope,
    KotlinTypeVariableContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility {

    internal typealias Block = KotlinTypeAliasScope.() -> Unit

    internal val typeVariableContainer = KotlinTypeVariableContainerInternal { specBuilder.addTypeVariable(it) }
    internal val annotationContainer = KotlinAnnotationContainerInternal { specBuilder.addAnnotation(it) }
    internal val modifierContainer = KotlinModifierContainerInternal { specBuilder.addModifiers(it) }

    internal fun build() = specBuilder.build()

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(name: String, type: XTypeName) =
            KotlinTypeAliasScope(scope.config, KPTypeAlias.builder(name, type.interopToKotlin()))
    }
}
