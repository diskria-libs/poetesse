package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

class KotlinPropertyScope private constructor(
    override val config: Poetesse.Config,
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
        append = { specBuilder.addModifiers(it) }
    )

    private var getter: KPFunctionBuilder? = null
    private var setter: KPFunctionBuilder? = null
    private val accessorModifiers: MutableList<KPModifier> by lazy { mutableListOf() }

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)
    fun final() = modifier(KPModifier.FINAL)
    fun open() = modifier(KPModifier.OPEN)
    fun abstract() = modifier(KPModifier.ABSTRACT)
    fun const() = modifier(KPModifier.CONST)
    fun external() = modifier(KPModifier.EXTERNAL)
    fun override() = modifier(KPModifier.OVERRIDE)
    fun lateinit() = modifier(KPModifier.LATEINIT)

    fun initializer(block: KotlinCodeScope.Block) {
        specBuilder.initializer(KotlinCodeScope.of(block).codeBlock)
    }

    fun mutable(mutable: Boolean = true) {
        specBuilder.mutable(mutable)
    }

    fun inline(inline: Boolean = true) {
        if (!inline) return
        accessorModifiers += KPModifier.INLINE
    }

    fun getter(block: KotlinPropertyGetterScope.Block = {}) {
        getter = KotlinPropertyGetterScope.of().apply(block).specBuilder
    }

    fun fullSetter(block: KotlinPropertySetterScope.Block = {}) {
        setter = KotlinPropertySetterScope.of().apply(block).specBuilder
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
        context(scope: PoetesseXScope)
        fun of(name: String, type: XTypeName) =
            KotlinPropertyScope(scope.config, type, KPProperty.builder(name, type.interopToKotlin()))
    }
}
