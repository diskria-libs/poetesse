package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

class JavaTypeScope private constructor(
    override val config: Poetesse.Config,
    private val className: XClassName,
    private val builder: JPTypeBuilder,
) : PoetesseJavaScope,
    JavaTypeVariableContainer,
    JavaTypeContainer,
    JavaFieldContainer,
    JavaConstructorContainer,
    JavaMethodContainer,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility {

    internal typealias Block = JavaTypeScope.(className: XClassName) -> Unit

    internal val typeVariableContainer = JavaTypeVariableContainerInternal { builder.addTypeVariable(it) }
    internal val typeContainer = JavaTypeContainerInternal(
        append = { builder.addType(it) },
        nestedClassName = { name -> className.nested(name) },
    )
    internal val fieldContainer = JavaFieldContainerInternal { builder.addField(it) }
    internal val constructorContainer = JavaConstructorContainerInternal { builder.addMethod(it) }
    internal val methodContainer = JavaMethodContainerInternal { builder.addMethod(it) }
    internal val annotationContainer = JavaAnnotationContainerInternal { builder.addAnnotation(it) }
    internal val modifierContainer = JavaModifierContainerInternal { builder.addModifiers(it) }

    fun abstract() = modifier(JPModifier.ABSTRACT)
    fun static() = modifier(JPModifier.STATIC)
    fun nonSealed() = modifier(JPModifier.NON_SEALED)
    fun strictfp() = modifier(JPModifier.STRICTFP)

    fun sealed(permits: Iterable<XTypeName>) {
        modifier(JPModifier.SEALED)
        permits.forEach {
            builder.addPermittedSubclass(it.interopToJava())
        }
    }

    fun sealed(vararg permits: XTypeName) = sealed(permits.asIterable())

    fun initializerBlock(block: JavaCodeBlockScope.Block = {}) {
        builder.addInitializerBlock(JavaCodeBlockScope.of().apply(block).build())
    }

    fun staticBlock(block: JavaCodeBlockScope.Block = {}) {
        builder.addStaticBlock(JavaCodeBlockScope.of().apply(block).build())
    }

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(kind: JPTypeKind, name: String, className: XClassName): JavaTypeScope {
            val builder = when (kind) {
                JPTypeKind.CLASS -> JPType.classBuilder(name)
                JPTypeKind.RECORD -> JPType.recordBuilder(name)
                JPTypeKind.INTERFACE -> JPType.interfaceBuilder(name)
                JPTypeKind.ENUM -> JPType.enumBuilder(name)
                JPTypeKind.ANNOTATION -> JPType.annotationBuilder(name)
            }
            return JavaTypeScope(poetesse.config, className, builder)
        }
    }
}
