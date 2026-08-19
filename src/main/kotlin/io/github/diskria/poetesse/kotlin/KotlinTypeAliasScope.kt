package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

class KotlinTypeAliasScope private constructor(
    override val config: Poetesse.Config,
    private val builder: KPTypeAliasBuilder,
) : PoetesseKotlinScope,
    KotlinDocumentationTrait,
    KotlinAnnotationTrait,
    KotlinModifierTrait.WithVisibility,
    KotlinTypeVariableTrait {

    internal typealias Block = KotlinTypeAliasScope.() -> Unit

    internal val documentationContainer = KotlinDocumentationContainer(builder::addKdoc)
    internal val annotationContainer = KotlinAnnotationContainer(builder::addAnnotation)
    internal val modifierContainer = KotlinModifierContainer(builder::addModifiers)
    internal val typeVariableContainer = KotlinTypeVariableContainer(builder::addTypeVariable)

    fun actual() = modifier(KPModifier.ACTUAL)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, type: XTypeName) =
            KotlinTypeAliasScope(poetesse.config, KPTypeAlias.builder(name, type.interopToKotlin()))
    }
}
