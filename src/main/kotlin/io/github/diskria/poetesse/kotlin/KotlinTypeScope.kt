package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.TypeSpec
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.XClassName

@PoetesseKotlin
class KotlinTypeScope private constructor(
    val className: XClassName,
    private val specBuilder: TypeSpec.Builder
) : KotlinModifierConfigScope.External,
    KotlinTypeContainerScope.External,
    KotlinFunctionContainerScope.External {

    internal val modifierConfigInternalScope = KotlinModifierConfigScope.Internal.of(
        append = { specBuilder.addModifiers(*it) }
    )
    internal val typeContainerInternalScope = KotlinTypeContainerScope.Internal.of(
        specHolderBuilder = specBuilder,
        nestedClassName = { name -> className.nested(name) },
    )
    internal val functionContainerInternalScope = KotlinFunctionContainerScope.Internal.of(
        specHolderBuilder = specBuilder
    )

    fun sealed() {
        modifiers(KPModifier.SEALED)
    }

    fun inner() {
        modifiers(KPModifier.INNER)
    }

    internal fun build(): TypeSpec =
        specBuilder.build()

    internal companion object {
        fun of(kind: TypeSpec.Kind, name: String, className: XClassName): KotlinTypeScope =
            KotlinTypeScope(
                className,
                when (kind) {
                    TypeSpec.Kind.CLASS -> TypeSpec.classBuilder(name)
                    TypeSpec.Kind.OBJECT -> TypeSpec.objectBuilder(name)
                    TypeSpec.Kind.INTERFACE -> TypeSpec.interfaceBuilder(name)
                }
            )
    }
}
