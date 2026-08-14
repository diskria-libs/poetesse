package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.addStatement
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

class KotlinFunctionScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: KPFunctionBuilder,
) : PoetesseKotlinScope,
    KotlinTypeVariableContainer,
    KotlinParameterContainer,
    KotlinAnnotationContainer,
    KotlinModifierContainer.WithVisibility,
    KotlinBodyContainer {

    internal val typeVariableContainer = KotlinTypeVariableContainerInternal.of(
        append = { specBuilder.addTypeVariable(it) }
    )
    internal val parameterContainer = KotlinParameterContainerInternal.of(
        append = { specBuilder.addParameter(it) }
    )
    internal val annotationContainer = KotlinAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )
    internal val bodyContainer = KotlinBodyContainerInternal.of(
        append = { specBuilder.addStatement(it) },
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

    fun override() {
        modifiers(KPModifier.OVERRIDE)
    }

    fun tailrec() {
        modifiers(KPModifier.TAILREC)
    }

    fun suspend() {
        modifiers(KPModifier.SUSPEND)
    }

    fun inline() {
        modifiers(KPModifier.INLINE)
    }

    fun infix() {
        modifiers(KPModifier.INFIX)
    }

    fun operator() {
        modifiers(KPModifier.OPERATOR)
    }

    fun returns(type: XTypeName<*, *>) {
        specBuilder.returns(type.interopToKotlin())
    }

    fun returns(type: KClass<*>, nullable: Boolean = false) =
        returns(xType(type, nullable = nullable))

    inline fun <reified T> returns(nullable: Boolean = true) =
        returns(T::class, nullable)

    inline fun <reified T : Any> returns() =
        returns<T>(nullable = false)

    internal fun build(): KPFunction =
        specBuilder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, name: String): KotlinFunctionScope =
            KotlinFunctionScope(settings, KPFunction.builder(name))
    }
}
