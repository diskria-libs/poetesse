package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

@PoetesseJava
class JavaTypeScope private constructor(
    override val settings: Poetesse.Settings,
    private val className: XClassName,
    private val specBuilder: JPTypeBuilder
) : JavaTypeVariableContainer,
    JavaTypeContainer,
    JavaFieldContainer,
    JavaConstructorContainer,
    JavaMethodContainer,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility {

    internal val typeVariableContainer = JavaTypeVariableContainerInternal.of(
        append = { specBuilder.addTypeVariable(it) }
    )
    internal val typeContainer = JavaTypeContainerInternal.of(
        append = { specBuilder.addType(it) },
        nestedClassName = { name -> className.nested(name) },
    )
    internal val fieldContainer = JavaFieldContainerInternal.of(
        append = { specBuilder.addField(it) }
    )
    internal val constructorContainer = JavaConstructorContainerInternal.of(
        append = { specBuilder.addMethod(it) }
    )
    internal val methodContainer = JavaMethodContainerInternal.of(
        append = { specBuilder.addMethod(it) }
    )
    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun abstract() {
        modifiers(JPModifier.ABSTRACT)
    }

    fun static() {
        modifiers(JPModifier.STATIC)
    }

    fun sealed(permits: Iterable<XTypeName<*, *>>) {
        modifiers(JPModifier.SEALED)
        permits.forEach {
            specBuilder.addPermittedSubclass(it.interopToJava())
        }
    }

    fun sealed(vararg permits: XTypeName<*, *>) {
        sealed(permits.asIterable())
    }

    fun nonSealed() {
        modifiers(JPModifier.NON_SEALED)
    }

    fun strictfp() {
        modifiers(JPModifier.STRICTFP)
    }

    fun initializerBlock(block: JavaCodeBlockBuilder) {
        specBuilder.addInitializerBlock(JavaCodeBlockScope.of(settings, block).build())
    }

    fun staticBlock(block: JavaCodeBlockBuilder) {
        specBuilder.addStaticBlock(JavaCodeBlockScope.of(settings, block).build())
    }

    internal fun build(): JPType =
        specBuilder.build()

    internal companion object {
        fun of(settings: Poetesse.Settings, kind: JPTypeKind, name: String, className: XClassName): JavaTypeScope =
            JavaTypeScope(
                settings,
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
