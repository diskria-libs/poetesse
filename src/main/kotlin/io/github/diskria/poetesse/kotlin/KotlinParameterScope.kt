package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

class KotlinParameterScope private constructor(
    override val config: Poetesse.Config,
    private val builder: KPParameterBuilder,
) : PoetesseKotlinScope,
    KotlinDocumentationTrait,
    KotlinAnnotationTrait,
    KotlinModifierTrait {

    internal typealias Block = KotlinParameterScope.() -> Unit

    internal val documentationContainer = KotlinDocumentationContainer(builder::addKdoc)
    internal val annotationContainer = KotlinAnnotationContainer(builder::addAnnotation)
    internal val modifierContainer = KotlinModifierContainer(builder::addModifiers)

    fun vararg() = modifier(KPModifier.VARARG)
    fun noinline() = modifier(KPModifier.NOINLINE)
    fun crossinline() = modifier(KPModifier.CROSSINLINE)

    fun defaultArgument(block: KotlinCodeScope.Block) {
        builder.defaultValue(KotlinCodeScope.of(block).codeBlock)
    }

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, type: XTypeName) =
            KotlinParameterScope(poetesse.config, KPParameter.builder(name, type.interopToKotlin()))
    }
}
