package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.applyCodeBlockMutation
import io.github.diskria.poetesse.interop.PoetesseScope

class KotlinPropertyGetterScope private constructor(
    override val config: Poetesse.Config,
    internal val builder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinDocumentationTrait,
    KotlinAnnotationTrait,
    KotlinModifierTrait.WithVisibility,
    KotlinBodyTrait {

    internal typealias Block = KotlinPropertyGetterScope.() -> Unit

    internal val documentationContainer by lazy { KotlinDocumentationContainer(builder::addKdoc) }
    internal val annotationContainer by lazy { KotlinAnnotationContainer(builder::addAnnotation) }
    internal val modifierContainer by lazy { KotlinModifierContainer(builder::addModifiers) }
    internal val statementContainer by lazy { KotlinBodyContainer(builder::applyCodeBlockMutation) }

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)
    fun inline() = modifier(KPModifier.INLINE)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of() = KotlinPropertyGetterScope(poetesse.config, KPFunction.getterBuilder())
    }
}
