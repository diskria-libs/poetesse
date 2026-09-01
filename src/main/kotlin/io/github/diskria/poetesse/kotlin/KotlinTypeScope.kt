package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.*
import kotlin.reflect.KClass

sealed class AbstractKotlinBodyScope(
    override val config: Poetesse.Config,
    protected val builder: KPTypeBuilder,
) : PoetesseKotlinScope,
    KotlinDocumentationTrait,
    KotlinAnnotationTrait,
    KotlinPropertyTrait,
    KotlinFunctionTrait,
    KotlinTypeTrait {

    protected abstract val classNameFactory: XClassName.Factory

    internal val documentationContainer by lazy { KotlinDocumentationContainer(builder::addKdoc) }
    internal val annotationContainer by lazy { KotlinAnnotationContainer(builder::addAnnotation) }
    internal val propertyContainer by lazy { KotlinPropertyContainer(builder::addProperty) }
    internal val functionContainer by lazy { KotlinFunctionContainer(builder::addFunction) }
    internal val typeContainer by lazy { KotlinTypeContainer(classNameFactory, builder::addType) }

    fun superinterface(type: XTypeName, by: KotlinCodeScope.Block? = null) {
        val kpTypeName = type.interopToKotlin()
        val delegate = by?.let { KotlinCodeScope.of(it).codeBlock }
        delegate?.let { builder.addSuperinterface(kpTypeName, it) } ?: builder.addSuperinterface(kpTypeName)
    }

    fun superinterface(type: KClass<*>, by: KotlinCodeScope.Block? = null) {
        superinterface(xType(type), by)
    }

    inline fun <reified T : Any> superinterface(noinline by: KotlinCodeScope.Block? = null) {
        superinterface(T::class, by)
    }

    fun init(block: KotlinCodeBlockScope.Block = {}) {
        builder.addInitializerBlock(KotlinCodeBlockScope.of().apply(block).build())
    }

    internal fun build() = builder.build()
}

class KotlinAnonymousBodyScope private constructor(
    config: Poetesse.Config,
    packageName: String?,
    builder: KPTypeBuilder,
) : AbstractKotlinBodyScope(config, builder) {

    internal typealias Block = KotlinAnonymousBodyScope.() -> Unit

    override val classNameFactory: XClassName.Factory = { name -> xClass(packageName, name) }

    fun superclass(type: XTypeName, block: KotlinSuperclassConstructorScope.Block = {}) {
        builder.superclass(type.interopToKotlin())
        KotlinSuperclassConstructorScope.of(builder).block()
    }

    fun superclass(type: KClass<*>, block: KotlinSuperclassConstructorScope.Block = {}) {
        superclass(xType(type), block)
    }

    inline fun <reified T : Any> superclass(noinline block: KotlinSuperclassConstructorScope.Block = {}) {
        superclass(T::class, block)
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(packageName: String?): KotlinAnonymousBodyScope =
            KotlinAnonymousBodyScope(poetesse.config, packageName, KPType.anonymousClassBuilder())
    }
}

sealed class AbstractKotlinTypeScope(
    config: Poetesse.Config,
    protected val className: XClassName,
    builder: KPTypeBuilder,
) : AbstractKotlinBodyScope(config, builder),
    KotlinModifierTrait.WithVisibility,
    KotlinConstructorTrait,
    KotlinTypeAliasTrait {

    override val classNameFactory = className::nested

    internal val modifierContainer by lazy { KotlinModifierContainer(builder::addModifiers) }
    internal val constructorContainer by lazy {
        KotlinConstructorContainer(builder) { constructor, isPrimary ->
            if (isPrimary) builder.primaryConstructor(constructor)
            else builder.addFunction(constructor)
        }
    }
    internal val typeAliasContainer by lazy { KotlinTypeAliasContainer(classNameFactory, builder::addTypeAlias) }

    fun expect() = modifier(KPModifier.EXPECT)
    fun actual() = modifier(KPModifier.ACTUAL)

    fun companion_object(name: String? = null, block: KotlinCompanionObjectTypeScope.Block = {}) {
        val className = className.nested(name ?: "Companion")
        builder.addType(KotlinCompanionObjectTypeScope.of(name, className).apply { block(className) }.build())
    }
}

class KotlinTypeScope private constructor(
    config: Poetesse.Config,
    className: XClassName,
    builder: KPTypeBuilder,
) : AbstractKotlinTypeScope(config, className, builder),
    KotlinTypeVariableTrait {

    internal typealias Block = KotlinTypeScope.(className: XClassName) -> Unit

    internal val typeVariableContainer by lazy { KotlinTypeVariableContainer(builder::addTypeVariable) }

    fun final() = modifier(KPModifier.FINAL)
    fun open() = modifier(KPModifier.OPEN)
    fun abstract() = modifier(KPModifier.ABSTRACT)
    fun external() = modifier(KPModifier.EXTERNAL)
    fun sealed() = modifier(KPModifier.SEALED)
    fun inner() = modifier(KPModifier.INNER)

    fun superclass(type: XTypeName, block: KotlinSuperclassConstructorScope.Block = {}) {
        builder.superclass(type.interopToKotlin())
        KotlinSuperclassConstructorScope.of(builder).block()
    }

    fun superclass(type: KClass<*>, block: KotlinSuperclassConstructorScope.Block = {}) {
        superclass(xType(type), block)
    }

    inline fun <reified T : Any> superclass(noinline block: KotlinSuperclassConstructorScope.Block = {}) {
        superclass(T::class, block)
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(kind: KPTypeKind, name: String, className: XClassName): KotlinTypeScope {
            val builder = when (kind) {
                KPTypeKind.CLASS -> KPType.classBuilder(name)
                KPTypeKind.OBJECT -> KPType.objectBuilder(name)
                KPTypeKind.INTERFACE -> KPType.interfaceBuilder(name)
            }
            return KotlinTypeScope(poetesse.config, className, builder)
        }
    }
}

class KotlinEnumTypeScope private constructor(
    config: Poetesse.Config,
    className: XClassName,
    builder: KPTypeBuilder,
) : AbstractKotlinTypeScope(config, className, builder) {

    internal typealias Block = KotlinEnumTypeScope.(className: XClassName) -> Unit

    fun constant(name: String, block: ConstantScope.Block = {}) {
        builder.addEnumConstant(name, ConstantScope().apply(block).build())
    }

    inner class ConstantScope internal constructor(
        override val config: Poetesse.Config = this@KotlinEnumTypeScope.config,
        private val builder: KPTypeBuilder = KPType.anonymousClassBuilder(),
    ) : KotlinSuperclassConstructorScope(config, builder),
        KotlinPropertyTrait,
        KotlinFunctionTrait {

        internal typealias Block = ConstantScope.() -> Unit

        internal val propertyContainer by lazy { KotlinPropertyContainer(builder::addProperty) }
        internal val functionContainer by lazy { KotlinFunctionContainer(builder::addFunction) }

        internal fun build() = builder.build()
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, className: XClassName): KotlinEnumTypeScope =
            KotlinEnumTypeScope(poetesse.config, className, KPType.enumBuilder(name))
    }
}

class KotlinCompanionObjectTypeScope private constructor(
    config: Poetesse.Config,
    private val className: XClassName,
    builder: KPTypeBuilder,
) : AbstractKotlinBodyScope(config, builder),
    KotlinModifierTrait.WithVisibility,
    KotlinTypeAliasTrait {

    internal typealias Block = KotlinCompanionObjectTypeScope.(className: XClassName) -> Unit

    override val classNameFactory = className::nested

    internal val modifierContainer by lazy { KotlinModifierContainer(builder::addModifiers) }
    internal val typeAliasContainer by lazy { KotlinTypeAliasContainer(classNameFactory, builder::addTypeAlias) }

    fun external() = modifier(KPModifier.EXTERNAL)

    fun superclass(type: XTypeName, block: KotlinSuperclassConstructorScope.Block = {}) {
        builder.superclass(type.interopToKotlin())
        KotlinSuperclassConstructorScope.of(builder).block()
    }

    fun superclass(type: KClass<*>, block: KotlinSuperclassConstructorScope.Block = {}) {
        superclass(xType(type), block)
    }

    inline fun <reified T : Any> superclass(noinline block: KotlinSuperclassConstructorScope.Block = {}) {
        superclass(T::class, block)
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String?, className: XClassName): KotlinCompanionObjectTypeScope =
            KotlinCompanionObjectTypeScope(poetesse.config, className, KPType.companionObjectBuilder(name))
    }
}
