package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope
import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

class JavaTypeScope private constructor(
    override val config: Poetesse.Config,
    private val className: XClassName,
    private val specBuilder: JPTypeBuilder,
) : PoetesseJavaScope,
    JavaTypeVariableContainer,
    JavaTypeContainer,
    JavaFieldContainer,
    JavaConstructorContainer,
    JavaMethodContainer,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility {

    internal typealias Block = JavaTypeScope.(className: XClassName) -> Unit

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
        append = { specBuilder.addModifiers(it) }
    )

    fun abstract() = modifier(JPModifier.ABSTRACT)
    fun static() = modifier(JPModifier.STATIC)
    fun nonSealed() = modifier(JPModifier.NON_SEALED)
    fun strictfp() = modifier(JPModifier.STRICTFP)

    fun sealed(permits: Iterable<XTypeName>) {
        modifier(JPModifier.SEALED)
        permits.forEach {
            specBuilder.addPermittedSubclass(it.interopToJava())
        }
    }

    fun sealed(vararg permits: XTypeName) = sealed(permits.asIterable())

    fun initializerBlock(block: JavaCodeBlockScope.Block = {}) {
        specBuilder.addInitializerBlock(JavaCodeBlockScope.of(block).build())
    }

    fun staticBlock(block: JavaCodeBlockScope.Block = {}) {
        specBuilder.addStaticBlock(JavaCodeBlockScope.of(block).build())
    }

    internal fun build() = specBuilder.build()

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(kind: JPTypeKind, name: String, className: XClassName): JavaTypeScope {
            val specBuilder = when (kind) {
                JPTypeKind.CLASS -> JPType.classBuilder(name)
                JPTypeKind.RECORD -> JPType.recordBuilder(name)
                JPTypeKind.INTERFACE -> JPType.interfaceBuilder(name)
                JPTypeKind.ENUM -> JPType.enumBuilder(name)
                JPTypeKind.ANNOTATION -> JPType.annotationBuilder(name)
            }
            return JavaTypeScope(scope.config, className, specBuilder)
        }
    }
}
