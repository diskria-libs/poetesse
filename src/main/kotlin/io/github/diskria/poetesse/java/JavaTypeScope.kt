package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.*
import kotlin.reflect.KClass

class JavaTypeScope private constructor(
    override val config: Poetesse.Config,
    private val className: XClassName,
    private val builder: JPTypeBuilder,
) : PoetesseJavaScope,
    JavaDocumentationTrait,
    JavaAnnotationTrait,
    JavaModifierTrait.WithVisibility,
    JavaTypeVariableTrait,
    JavaFieldTrait,
    JavaConstructorTrait,
    JavaMethodTrait,
    JavaTypeTrait {

    internal typealias Block = JavaTypeScope.(className: XClassName) -> Unit

    internal val documentationContainer by lazy { JavaDocumentationContainer(builder::addJavadoc) }
    internal val annotationContainer by lazy { JavaAnnotationContainer(builder::addAnnotation) }
    internal val modifierContainer by lazy { JavaModifierContainer(builder::addModifiers) }
    internal val typeVariableContainer by lazy { JavaTypeVariableContainer(builder::addTypeVariable) }
    internal val fieldContainer by lazy { JavaFieldContainer(builder::addField) }
    internal val constructorContainer by lazy { JavaConstructorContainer(builder::addMethod) }
    internal val methodContainer by lazy { JavaMethodContainer(builder::addMethod) }
    internal val typeContainer by lazy { JavaTypeContainer(className::nested, builder::addType) }

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

    fun superclass(type: XTypeName) {
        builder.superclass(type.interopToJava())
    }

    fun superclass(type: KClass<*>) {
        superclass(xType(type))
    }

    inline fun <reified T : Any> superclass() {
        superclass(T::class)
    }

    fun superinterface(type: XTypeName) {
        builder.addSuperinterface(type.interopToJava())
    }

    fun superinterface(type: KClass<*>) {
        superinterface(xType(type))
    }

    inline fun <reified T : Any> superinterface() {
        superinterface(T::class)
    }

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
