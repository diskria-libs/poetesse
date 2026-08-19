package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

class KotlinPropertyScope private constructor(
    override val config: Poetesse.Config,
    private val type: XTypeName,
    private val builder: KPPropertyBuilder,
) : PoetesseKotlinScope,
    KotlinDocumentationTrait,
    KotlinContextParameterTrait,
    KotlinAnnotationTrait,
    KotlinModifierTrait.WithVisibility,
    KotlinTypeVariableTrait,
    KotlinExtensionReceiverTrait {

    internal typealias Block = KotlinPropertyScope.() -> Unit

    internal val documentationContainer = KotlinDocumentationContainer(builder::addKdoc)
    internal val contextParameterContainer = KotlinContextParameterContainer(builder::contextParameter)
    internal val annotationContainer = KotlinAnnotationContainer(builder::addAnnotation)
    internal val modifierContainer = KotlinModifierContainer(builder::addModifiers)
    internal val typeVariableContainer = KotlinTypeVariableContainer(builder::addTypeVariable)
    internal val extensionReceiverContainer = KotlinExtensionReceiverContainer(builder::receiver)

    private val accessorModifiers: MutableList<KPModifier> by lazy { mutableListOf() }
    private var getter: KPFunctionBuilder? = null
    private var setter: KPFunctionBuilder? = null

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)
    fun final() = modifier(KPModifier.FINAL)
    fun open() = modifier(KPModifier.OPEN)
    fun abstract() = modifier(KPModifier.ABSTRACT)
    fun const() = modifier(KPModifier.CONST)
    fun external() = modifier(KPModifier.EXTERNAL)
    fun override() = modifier(KPModifier.OVERRIDE)
    fun lateinit() = modifier(KPModifier.LATEINIT)

    fun initializer(by: Boolean = false, block: KotlinCodeScope.Block) {
        val codeBlock = KotlinCodeScope.of(block).codeBlock
        if (by) builder.delegate(codeBlock) else builder.initializer(codeBlock)
    }

    fun mutable(mutable: Boolean = true) {
        builder.mutable(mutable)
    }

    fun inline(inline: Boolean = true) {
        if (!inline) return
        accessorModifiers += KPModifier.INLINE
    }

    fun getter(block: KotlinPropertyGetterScope.Block = {}) {
        getter = KotlinPropertyGetterScope.of().apply(block).builder
    }

    fun fullSetter(block: KotlinPropertySetterScope.Block = {}) {
        setter = KotlinPropertySetterScope.of().apply(block).builder
    }

    fun setter(parameterName: String = "value", block: KotlinPropertySetterScope.(String) -> Unit = {}) {
        fullSetter {
            parameter(parameterName, this@KotlinPropertyScope.type)
            block(parameterName)
        }
    }

    internal fun build(): KPProperty =
        builder.apply {
            getter?.let { getter(it.addModifiers(accessorModifiers).build()) }
            setter?.let { mutable().setter(it.addModifiers(accessorModifiers).build()) }
        }.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, type: XTypeName) =
            KotlinPropertyScope(poetesse.config, type, KPProperty.builder(name, type.interopToKotlin()))
    }
}
