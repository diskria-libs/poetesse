package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.interop.XClassName

@PoetesseKotlin
class KotlinTypeScope private constructor(
    val className: XClassName,
    private val specBuilder: KPTypeBuilder
) : KotlinTypeContainer,
    KotlinFunctionContainer,
    KotlinModifierContainer {

    internal val typeContainer = KotlinTypeContainerInternal.of(
        holderBuilder = specBuilder,
        nestedClassName = { name -> className.nested(name) },
    )
    internal val functionContainer = KotlinFunctionContainerInternal.of(
        holderBuilder = specBuilder
    )
    internal val modifierContainer = KotlinModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun sealed() {
        modifiers(KPModifier.SEALED)
    }

    fun inner() {
        modifiers(KPModifier.INNER)
    }

    internal fun build(): KPType =
        specBuilder.build()

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
