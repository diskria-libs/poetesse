package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.*
import kotlin.reflect.KClass

class KotlinTypeScope private constructor(
    override val config: Poetesse.Config,
    private val className: XClassName,
    private val builder: KPTypeBuilder,
) : PoetesseKotlinScope,
    KotlinTypeVariableTrait,
    KotlinTypeTrait,
    KotlinTypeAliasTrait,
    KotlinPropertyTrait,
    KotlinConstructorTrait,
    KotlinFunctionTrait,
    KotlinAnnotationTrait,
    KotlinModifierTrait.WithVisibility {

    internal typealias Block = KotlinTypeScope.(className: XClassName) -> Unit

    internal val typeVariableContainer = KotlinTypeVariableContainerInternal(builder::addTypeVariable)
    internal val typeContainer = KotlinTypeContainer(className::nested, builder::addType)
    internal val typeAliasContainer = KotlinTypeAliasContainer(className::nested, builder::addTypeAlias)
    internal val propertyContainer = KotlinPropertyContainer(builder::addProperty)
    internal val constructorContainer = KotlinConstructorContainer(builder) { constructor, isPrimary ->
        if (isPrimary) builder.primaryConstructor(constructor)
        else builder.addFunction(constructor)
    }
    internal val functionContainer = KotlinFunctionContainer(builder::addFunction)
    internal val modifierContainer = KotlinModifierContainerInternal(builder::addModifiers)
    internal val annotationContainer = KotlinAnnotationContainer(builder::addAnnotation)

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)
    fun final() = modifier(KPModifier.FINAL)
    fun open() = modifier(KPModifier.OPEN)
    fun abstract() = modifier(KPModifier.ABSTRACT)
    fun external() = modifier(KPModifier.EXTERNAL)
    fun sealed() = modifier(KPModifier.SEALED)
    fun inner() = modifier(KPModifier.INNER)

    fun superclass(type: XTypeName, block: SuperclassConstructorScope.Block = {}) {
        builder.superclass(type.interopToKotlin())
        SuperclassConstructorScope(config).block()
    }

    fun superclass(type: KClass<*>, block: SuperclassConstructorScope.Block = {}) {
        superclass(xType(type), block)
    }

    inline fun <reified T : Any> superclass(noinline block: SuperclassConstructorScope.Block = {}) {
        superclass(T::class, block)
    }

    fun superinterface(type: XTypeName) {
        builder.addSuperinterface(type.interopToKotlin())
    }

    fun superinterface(type: KClass<*>) {
        superinterface(xType(type))
    }

    inline fun <reified T : Any> superinterface() {
        superinterface(T::class)
    }

    fun initializerBlock(block: KotlinCodeBlockScope.Block = {}) {
        builder.addInitializerBlock(KotlinCodeBlockScope.of().apply(block).build())
    }

    internal fun build() = builder.build()

    inner class SuperclassConstructorScope(
        override val config: Poetesse.Config
    ) : KotlinArgumentTrait {

        internal typealias Block = SuperclassConstructorScope.() -> Unit

        internal val argumentsContainer = KotlinArgumentsContainerInternal(
            this@KotlinTypeScope.builder::addSuperclassConstructorParameter
        )
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(kind: KPTypeKind, name: String, className: XClassName): KotlinTypeScope {
            val builder = when (kind) {
                KPTypeKind.CLASS -> KPType.classBuilder(name)
                KPTypeKind.OBJECT -> KPType.objectBuilder(name)
                KPTypeKind.INTERFACE -> KPType.interfaceBuilder(name)
            }
            return KotlinTypeScope(poetesse.config, className, builder)
        }
    }
}
