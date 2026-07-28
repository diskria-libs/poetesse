package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName

@PoetesseJava
class JavaTypeScope private constructor(
    val className: XClassName,
    private val specBuilder: JPTypeBuilder
) : JavaAnnotationConfigScope.External,
    JavaModifierConfigScope.External,
    JavaMethodContainerScope.External,
    JavaTypeContainerScope.External {

    @PublishedApi
    internal val annotationConfigInternalScope = JavaAnnotationConfigScope.Internal.of(
        append = { specBuilder.addAnnotation(it.spec) },
    )
    internal val modifierConfigInternalScope = JavaModifierConfigScope.Internal.of(
        append = { specBuilder.addModifiers(*it) }
    )
    internal val methodContainerInternalScope = JavaMethodContainerScope.Internal.of(
        append = { specBuilder.addMethod(it) }
    )
    internal val typeContainerInternalScope = JavaTypeContainerScope.Internal.of(
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
