package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName

@PoetesseJava
class JavaTypeScope private constructor(
    val className: XClassName,
    private val specBuilder: JPTypeBuilder
) : JavaAnnotationContainer,
    JavaModifierContainer,
    JavaMethodContainer,
    JavaTypeContainer {

    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )
    internal val methodContainer = JavaMethodContainerInternal.of(
        append = { specBuilder.addMethod(it) }
    )
    internal val typeContainer = JavaTypeContainerInternal.of(
        append = { specBuilder.addType(it) },
        nestedClassName = { name -> className.nested(name) },
    )

    fun abstract() {
        modifiers(JPModifier.ABSTRACT)
    }

    fun static() {
        modifiers(JPModifier.STATIC)
    }

    fun sealed() {
        modifiers(JPModifier.SEALED)
    }

    fun nonSealed() {
        modifiers(JPModifier.NON_SEALED)
    }

    fun strictfp() {
        modifiers(JPModifier.STRICTFP)
    }

    internal fun build(): JPType =
        specBuilder.build()

    internal companion object {
        fun of(kind: JPTypeKind, name: String, className: XClassName): JavaTypeScope =
            JavaTypeScope(
                className,
                when (kind) {
                    JPTypeKind.CLASS -> JPType.classBuilder(name)
                    JPTypeKind.RECORD -> JPType.recordBuilder(name)
                    JPTypeKind.INTERFACE -> JPType.interfaceBuilder(name)
                    JPTypeKind.ENUM -> JPType.enumBuilder(name)
                    JPTypeKind.ANNOTATION -> JPType.annotationBuilder(name)
                }
            )
    }
}
