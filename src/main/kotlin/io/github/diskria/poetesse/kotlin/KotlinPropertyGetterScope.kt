package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.extensions.addStatement

@PoetesseKotlin
class KotlinPropertyGetterScope private constructor(
    override val settings: Poetesse.Settings,
    internal val specBuilder: KPFunctionBuilder
) : KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility {

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

    fun inline() {
        modifiers(KPModifier.INLINE)
    }

    fun body(block: BodyScope.() -> Unit) {
        BodyScope(settings).apply(block)
    }

    fun expression(block: KotlinCodeBuilder) {
        body { line { "return ${L(block)}" } }
    }

    internal fun build(): KPFunction =
        specBuilder.build()

    @PoetesseKotlin
    inner class BodyScope(override val settings: Poetesse.Settings) : KotlinCodeBlockContainer {
        internal val codeBlockContainer = KotlinCodeBlockContainerInternal.of(
            append = { this@KotlinPropertyGetterScope.specBuilder.addStatement(it) }
        )
    }

    internal companion object {
        fun of(settings: Poetesse.Settings): KotlinPropertyGetterScope =
            KotlinPropertyGetterScope(settings, KPFunction.getterBuilder())
    }
}
