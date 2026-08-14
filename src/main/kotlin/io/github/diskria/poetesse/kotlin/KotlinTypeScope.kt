package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

class KotlinTypeScope private constructor(
    override val settings: Poetesse.Settings,
    private val className: XClassName,
    private val specBuilder: KPTypeBuilder,
) : PoetesseKotlinScope,
    KotlinTypeVariableContainer,
    KotlinTypeContainer,
    KotlinPropertyContainer,
    KotlinConstructorContainer,
    KotlinFunctionContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility {

    internal typealias Block = KotlinTypeScope.(className: XClassName) -> Unit

    internal val typeVariableContainer = KotlinTypeVariableContainerInternal.of(
        append = { specBuilder.addTypeVariable(it) }
    )
    internal val typeContainer = KotlinTypeContainerInternal.of(
        appendType = { specBuilder.addType(it) },
        appendTypeAlias = { specBuilder.addTypeAlias(it) },
        nestedClassName = { name -> className.nested(name) },
    )
    internal val propertyContainer = KotlinPropertyContainerInternal.of(
        append = { specBuilder.addProperty(it) }
    )
    internal val constructorContainer = KotlinConstructorContainerInternal.of(
        append = { constructor, isPrimary ->
            if (isPrimary) specBuilder.primaryConstructor(constructor)
            else specBuilder.addFunction(constructor)
        }
    )
    internal val functionContainer = KotlinFunctionContainerInternal.of(
        append = { specBuilder.addFunction(it) }
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )
    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) }
    )

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

    fun external() {
        modifiers(KPModifier.EXTERNAL)
    }

    fun sealed() {
        modifiers(KPModifier.SEALED)
    }

    fun inner() {
        modifiers(KPModifier.INNER)
    }

    fun superclass(type: XTypeName, constructor: SuperclassConstructorScope.Block = {}) {
        specBuilder.superclass(type.interopToKotlin())
        SuperclassConstructorScope(settings).constructor()
    }

    fun superclass(type: KClass<*>, constructor: SuperclassConstructorScope.Block = {}) {
        superclass(xType(type), constructor)
    }

    inline fun <reified T : Any> superclass(noinline constructor: SuperclassConstructorScope.Block = {}) {
        superclass(T::class, constructor)
    }

    fun superinterface(type: XTypeName) {
        specBuilder.addSuperinterface(type.interopToKotlin())
    }

    fun superinterface(type: KClass<*>) {
        superinterface(xType(type))
    }

    inline fun <reified T : Any> superinterface() {
        superinterface(T::class)
    }

    fun superinterfaces(types: Iterable<XTypeName>) {
        types.forEach { superinterface(it) }
    }

    fun superinterfaces(vararg types: XTypeName) {
        superinterfaces(types.asIterable())
    }

    fun initializerBlock(block: KotlinCodeBlockScope.Block = {}) {
        specBuilder.addInitializerBlock(KotlinCodeBlockScope.of(settings, block).build())
    }

    internal fun build(): KPType =
        specBuilder.build()

    inner class SuperclassConstructorScope(override val settings: Poetesse.Settings) : KotlinArgumentsContainer {

        internal typealias Block = SuperclassConstructorScope.() -> Unit

        internal val argumentsContainer = KotlinArgumentsContainerInternal.of(
            append = { this@KotlinTypeScope.specBuilder.addSuperclassConstructorParameter(it) }
        )
    }

    internal companion object {
        fun of(settings: Poetesse.Settings, kind: KPTypeKind, name: String, className: XClassName) =
            KotlinTypeScope(
                settings,
                className,
                when (kind) {
                    KPTypeKind.CLASS -> KPType.classBuilder(name)
                    KPTypeKind.OBJECT -> KPType.objectBuilder(name)
                    KPTypeKind.INTERFACE -> KPType.interfaceBuilder(name)
                }
            )
    }
}
