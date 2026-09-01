package io.github.diskria.poetesse.java

import com.palantir.javapoet.TypeSpec
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.*
import kotlin.reflect.KClass

sealed class AbstractJavaBodyScope(
    override val config: Poetesse.Config,
    protected val builder: JPTypeBuilder,
) : PoetesseJavaScope,
    JavaDocumentationTrait,
    JavaAnnotationTrait,
    JavaFieldTrait,
    JavaMethodTrait,
    JavaTypeTrait {

    protected abstract val classNameFactory: XClassName.Factory

    internal val documentationContainer by lazy { JavaDocumentationContainer(builder::addJavadoc) }
    internal val annotationContainer by lazy { JavaAnnotationContainer(builder::addAnnotation) }
    internal val fieldContainer by lazy { JavaFieldContainer(builder::addField) }
    internal val methodContainer by lazy { JavaMethodContainer(builder::addMethod) }
    internal val typeContainer by lazy { JavaTypeContainer(classNameFactory, builder::addType) }

    fun superinterface(type: XTypeName) {
        builder.addSuperinterface(type.interopToJava())
    }

    fun superinterface(type: KClass<*>) {
        superinterface(xType(type))
    }

    inline fun <reified T : Any> superinterface() {
        superinterface(T::class)
    }

    fun init(block: JavaCodeBlockScope.Block = {}) {
        builder.addInitializerBlock(JavaCodeBlockScope.of().apply(block).build())
    }

    internal open fun build() = builder.build()
}

class JavaAnonymousBodyScope private constructor(
    config: Poetesse.Config,
    packageName: String?,
) : AbstractJavaBodyScope(config, JPType.anonymousClassBuilder("")) {

    internal typealias Block = JavaAnonymousBodyScope.() -> Unit

    override val classNameFactory: XClassName.Factory = { name -> xClass(packageName, name) }

    private var superclassArguments: JPCodeBlock = JPCodeBlock.of("")

    fun superclass(type: XTypeName, block: JavaSuperclassConstructorScope.Block = {}) {
        builder.superclass(type.interopToJava())
        superclassArguments = JavaSuperclassConstructorScope.of().apply(block).joinArguments()
    }

    fun superclass(type: KClass<*>, block: JavaSuperclassConstructorScope.Block = {}) {
        superclass(xType(type), block)
    }

    inline fun <reified T : Any> superclass(noinline block: JavaSuperclassConstructorScope.Block = {}) {
        superclass(T::class, block)
    }

    override fun build(): JPType {
        val source = builder.build()
        return TypeSpec.anonymousClassBuilder(superclassArguments).apply {
            addJavadoc(source.javadoc())
            addAnnotations(source.annotations())
            addFields(source.fieldSpecs())
            addMethods(source.methodSpecs())
            addTypes(source.typeSpecs())
            source.superclass()?.let { superclass(it) }
            source.superinterfaces().forEach { addSuperinterface(it) }
            source.initializerBlock().takeIf { !it.isEmpty }?.let { addInitializerBlock(it) }
        }.build()
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(packageName: String?): JavaAnonymousBodyScope =
            JavaAnonymousBodyScope(poetesse.config, packageName)
    }
}

sealed class AbstractJavaTypeScope(
    config: Poetesse.Config,
    protected val className: XClassName,
    builder: JPTypeBuilder,
) : AbstractJavaBodyScope(config, builder),
    JavaModifierTrait.WithVisibility,
    JavaConstructorTrait {

    override val classNameFactory = className::nested

    internal val modifierContainer by lazy { JavaModifierContainer(builder::addModifiers) }
    internal val constructorContainer by lazy { JavaConstructorContainer(builder::addMethod) }

    fun static(block: JavaCodeBlockScope.Block = {}) {
        builder.addStaticBlock(JavaCodeBlockScope.of().apply(block).build())
    }
}

class JavaTypeScope private constructor(
    override val config: Poetesse.Config,
    className: XClassName,
    builder: JPTypeBuilder,
) : AbstractJavaTypeScope(config, className, builder),
    JavaModifierTrait.WithVisibility,
    JavaTypeVariableTrait {

    internal typealias Block = JavaTypeScope.(className: XClassName) -> Unit

    internal val typeVariableContainer by lazy { JavaTypeVariableContainer(builder::addTypeVariable) }

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

class JavaEnumTypeScope private constructor(
    config: Poetesse.Config,
    className: XClassName,
    builder: JPTypeBuilder,
) : AbstractJavaTypeScope(config, className, builder) {

    internal typealias Block = JavaEnumTypeScope.(className: XClassName) -> Unit

    fun constant(name: String, block: ConstantScope.Block = {}) {
        builder.addEnumConstant(name, ConstantScope().apply(block).build())
    }

    inner class ConstantScope internal constructor(
        override val config: Poetesse.Config = this@JavaEnumTypeScope.config,
    ) : JavaSuperclassConstructorScope(config),
        JavaFieldTrait,
        JavaMethodTrait {

        internal typealias Block = ConstantScope.() -> Unit

        private val fields: MutableList<JPField> = mutableListOf()
        private val methods: MutableList<JPMethod> = mutableListOf()

        internal val fieldContainer by lazy { JavaFieldContainer { fields += it } }
        internal val methodContainer by lazy { JavaMethodContainer { methods += it } }

        internal fun build() =
            JPType.anonymousClassBuilder(joinArguments()).addFields(fields).addMethods(methods).build()
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, className: XClassName): JavaEnumTypeScope =
            JavaEnumTypeScope(poetesse.config, className, JPType.enumBuilder(name))
    }
}
