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
    JavaTypeVariableTrait,
    JavaTypeTrait,
    JavaFieldTrait,
    JavaConstructorTrait,
    JavaMethodTrait,
    JavaAnnotationTrait,
    JavaModifierTrait.WithVisibility {

    internal typealias Block = JavaTypeScope.(className: XClassName) -> Unit

    internal val typeVariableContainer = JavaTypeVariableContainerInternal(builder::addTypeVariable)
    internal val typeContainer = JavaTypeContainer(className::nested, builder::addType)
    internal val fieldContainer = JavaFieldContainer(builder::addField)
    internal val constructorContainer = JavaConstructorContainer(builder::addMethod)
    internal val methodContainer = JavaMethodContainer(builder::addMethod)
    internal val annotationContainer = JavaAnnotationContainer(builder::addAnnotation)
    internal val modifierContainer = JavaModifierContainerInternal(builder::addModifiers)

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
