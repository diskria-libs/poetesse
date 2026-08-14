package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

class KotlinPropertyScope private constructor(
    override val settings: Poetesse.Settings,
    private val type: XTypeName,
    private val specBuilder: KPPropertyBuilder,
) : PoetesseKotlinScope,
    KotlinTypeVariableContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility {

    internal typealias Block = KotlinPropertyScope.() -> Unit

    internal val typeVariableContainer = KotlinTypeVariableContainerInternal.of(
        append = { specBuilder.addTypeVariable(it) }
    )
    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    private var getter: KPFunctionBuilder? = null
    private var setter: KPFunctionBuilder? = null
    private val accessorModifiers: MutableList<KPModifier> by lazy { mutableListOf() }

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

    fun initializer(block: KotlinCodeBuilder) {
        specBuilder.initializer(KotlinCodeScope.of(settings, block).codeBlock)
    }

    fun mutable(mutable: Boolean = true) {
        specBuilder.mutable(mutable)
    }

    fun inline(inline: Boolean = true) {
        if (!inline) return
        accessorModifiers += KPModifier.INLINE
    }

    fun getter(block: KotlinPropertyGetterScope.Block = {}) {
        getter = KotlinPropertyGetterScope.of(settings).apply(block).specBuilder
    }

    fun fullSetter(block: KotlinPropertySetterScope.Block = {}) {
        setter = KotlinPropertySetterScope.of(settings).apply(block).specBuilder
    }

    fun setter(parameterName: String = "value", block: KotlinPropertySetterScope.(String) -> Unit = {}) {
        fullSetter {
            parameter(parameterName, this@KotlinPropertyScope.type)
            block(parameterName)
        }
    }

    internal fun build(): KPProperty =
        specBuilder.apply {
            getter?.let { getter(it.addModifiers(accessorModifiers).build()) }
            setter?.let {
                mutable()
                setter(it.addModifiers(accessorModifiers).build())
            }
        }.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, name: String, type: XTypeName) =
            KotlinPropertyScope(settings, type, KPProperty.builder(name, type.interopToKotlin()))
    }
}
