package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.*
import kotlin.reflect.KClass

class KotlinTypeScope private constructor(
    override val config: Poetesse.Config,
    private val className: XClassName,
    private val builder: KPTypeBuilder,
) : PoetesseKotlinScope,
    KotlinTypeVariableContainer,
    KotlinTypeContainer,
    KotlinPropertyContainer,
    KotlinConstructorContainer,
    KotlinFunctionContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility {

    internal typealias Block = KotlinTypeScope.(className: XClassName) -> Unit

    internal val typeVariableContainer = KotlinTypeVariableContainerInternal { builder.addTypeVariable(it) }
    internal val typeContainer = KotlinTypeContainerInternal(
        appendType = { builder.addType(it) },
        appendTypeAlias = { builder.addTypeAlias(it) },
        nestedClassName = { name -> className.nested(name) },
    )
    internal val propertyContainer = KotlinPropertyContainerInternal { builder.addProperty(it) }
    internal val constructorContainer = KotlinConstructorContainerInternal(builder) { constructor, isPrimary ->
        if (isPrimary) builder.primaryConstructor(constructor)
        else builder.addFunction(constructor)
    }
    internal val functionContainer = KotlinFunctionContainerInternal { builder.addFunction(it) }
    internal val modifierContainer = KotlinModifierContainerInternal { builder.addModifiers(it) }
    internal val annotationContainer = KotlinAnnotationContainerInternal { builder.addAnnotation(it) }

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
    ) : KotlinArgumentsContainer {

        internal typealias Block = SuperclassConstructorScope.() -> Unit

        internal val argumentsContainer = KotlinArgumentsContainerInternal {
            this@KotlinTypeScope.builder.addSuperclassConstructorParameter(it)
        }
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
