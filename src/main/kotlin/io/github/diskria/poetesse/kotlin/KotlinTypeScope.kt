package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

@PoetesseKotlin
class KotlinTypeScope private constructor(
    val className: XClassName,
    private val specBuilder: KPTypeBuilder
) : KotlinTypeContainer,
    // properties
    KotlinConstructorContainer,
    KotlinFunctionContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility {

    internal val typeContainer = KotlinTypeContainerInternal.of(
        append = { specBuilder.addType(it) },
        nestedClassName = { name -> className.nested(name) },
    )

    // properties

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

    fun superclass(type: XTypeName, constructor: SuperclassConstructorScope.() -> Unit = {}) {
        specBuilder.superclass(type.interopToKotlin())
        SuperclassConstructorScope().constructor()
    }

    fun superclass(type: KClass<*>, constructor: SuperclassConstructorScope.() -> Unit = {}) {
        superclass(type.xType(), constructor)
    }

    inline fun <reified T : Any> superclass(noinline constructor: SuperclassConstructorScope.() -> Unit = {}) {
        superclass(T::class, constructor)
    }

    fun superinterface(type: XTypeName) {
        specBuilder.addSuperinterface(type.interopToKotlin())
    }

    fun superinterface(type: KClass<*>) {
        superinterface(type.xType())
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

    internal fun build(): KPType =
        specBuilder.build()

    inner class SuperclassConstructorScope : KotlinArgumentsContainer {

        internal val argumentsContainer = KotlinArgumentsContainerInternal.of(
            append = { specBuilder.addSuperclassConstructorParameter(it) }
        )
    }

    internal companion object {
        fun of(kind: KPTypeKind, name: String, className: XClassName): KotlinTypeScope =
            KotlinTypeScope(
                className,
                when (kind) {
                    KPTypeKind.CLASS -> KPType.classBuilder(name)
                    KPTypeKind.OBJECT -> KPType.objectBuilder(name)
                    KPTypeKind.INTERFACE -> KPType.interfaceBuilder(name)
                }
            )
    }
}
