package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.appendCommand
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

class KotlinFunctionScope private constructor(
    override val config: Poetesse.Config,
    private val builder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinDocumentationTrait,
    KotlinContextParameterTrait,
    KotlinAnnotationTrait,
    KotlinModifierTrait.WithVisibility,
    KotlinTypeVariableTrait,
    KotlinExtensionReceiverTrait,
    KotlinParameterTrait,
    KotlinBodyTrait {

    internal typealias Block = KotlinFunctionScope.() -> Unit

    internal val documentationContainer = KotlinDocumentationContainer(builder::addKdoc)
    internal val contextParameterContainer = KotlinContextParameterContainer(builder::contextParameter)
    internal val annotationContainer = KotlinAnnotationContainer(builder::addAnnotation)
    internal val modifierContainer = KotlinModifierContainer(builder::addModifiers)
    internal val typeVariableContainer = KotlinTypeVariableContainer(builder::addTypeVariable)
    internal val extensionReceiverContainer = KotlinExtensionReceiverContainer(builder::receiver)
    internal val parameterContainer = KotlinParameterContainer(builder::addParameter)
    internal val statementContainer = KotlinBodyContainer(builder::appendCommand)

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)
    fun final() = modifier(KPModifier.FINAL)
    fun open() = modifier(KPModifier.OPEN)
    fun abstract() = modifier(KPModifier.ABSTRACT)
    fun external() = modifier(KPModifier.EXTERNAL)
    fun override() = modifier(KPModifier.OVERRIDE)
    fun tailrec() = modifier(KPModifier.TAILREC)
    fun suspend() = modifier(KPModifier.SUSPEND)
    fun inline() = modifier(KPModifier.INLINE)
    fun infix() = modifier(KPModifier.INFIX)
    fun operator() = modifier(KPModifier.OPERATOR)

    fun returns(type: XTypeName) {
        builder.returns(type.interopToKotlin())
    }

    fun returns(type: KClass<*>, nullable: Boolean = false) =
        returns(xType(type, nullable = nullable))

    inline fun <reified T> returns(nullable: Boolean = true) =
        returns(T::class, nullable)

    inline fun <reified T : Any> returns() =
        returns<T>(nullable = false)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String) = KotlinFunctionScope(poetesse.config, KPFunction.builder(name))
    }
}
