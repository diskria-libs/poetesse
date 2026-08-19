package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.MemberName.Companion.member
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.interopToKotlin
import io.github.diskria.poetesse.interop.xClass
import kotlin.reflect.KClass

class KotlinFileScope private constructor(
    override val config: Poetesse.Config,
    private val packageName: String?,
    val fileName: String,
    private val builder: KPFileBuilder,
) : PoetesseKotlinScope,
    KotlinTypeTrait,
    KotlinTypeAliasTrait,
    KotlinPropertyTrait,
    KotlinFunctionTrait,
    KotlinAnnotationTrait {

    internal typealias Block = KotlinFileScope.() -> Unit

    private val nestedClassNameFactory: (String) -> XClassName = { name -> xClass(packageName, name) }

    internal val typeContainer = KotlinTypeContainer(nestedClassNameFactory, builder::addType)
    internal val typeAliasContainer = KotlinTypeAliasContainer(nestedClassNameFactory, builder::addTypeAlias)
    internal val propertyContainer = KotlinPropertyContainer(builder::addProperty)
    internal val functionContainer = KotlinFunctionContainer(builder::addFunction)
    internal val annotationContainer = KotlinAnnotationContainer(builder::addAnnotation)

    fun import(className: XClassName, block: ImportScope.Block = {}) {
        val alias = ImportScope().apply(block).alias
        val kp = className.interopToKotlin()
        if (alias != null) {
            builder.addAliasedImport(kp, alias)
        } else {
            builder.addImport(kp.packageName, kp.simpleNames.joinToString("."))
        }
    }

    fun import(klass: KClass<*>, block: ImportScope.Block = {}) {
        import(xClass(klass), block)
    }

    inline fun <reified T : Any> import(noinline block: ImportScope.Block = {}) {
        import(T::class, block)
    }

    fun import(packageName: String?, name: String, block: ImportScope.Block = {}) {
        import(XClassName.of(packageName, name.split('.')), block)
    }

    fun import(packageName: String?, names: Iterable<String>) {
        names.forEach { import(packageName, it) }
    }

    fun import(packageName: String?, vararg names: String) {
        import(packageName, names.asIterable())
    }

    fun memberImport(owner: XClassName, name: String, block: ImportScope.Block = {}) {
        addMemberImport(owner.interopToKotlin().member(name), block)
    }

    fun memberImport(className: XClassName, names: Iterable<String>) {
        names.forEach { memberImport(className, it) }
    }

    fun memberImport(className: XClassName, vararg names: String) {
        memberImport(className, names.asIterable())
    }

    fun memberImport(owner: KClass<*>, name: String, block: ImportScope.Block = {}) {
        memberImport(xClass(owner), name, block)
    }

    fun memberImport(owner: KClass<*>, names: Iterable<String>) {
        names.forEach { memberImport(owner, it) }
    }

    fun memberImport(owner: KClass<*>, vararg names: String) {
        memberImport(owner, names.asIterable())
    }

    inline fun <reified Owner : Any> memberImport(name: String, noinline block: ImportScope.Block = {}) {
        memberImport(Owner::class, name, block)
    }

    inline fun <reified Owner : Any> memberImport(names: Iterable<String>) {
        names.forEach { memberImport<Owner>(it) }
    }

    inline fun <reified Owner : Any> memberImport(vararg names: String) {
        memberImport<Owner>(names.asIterable())
    }

    fun memberImport(packageName: String?, name: String, block: ImportScope.Block = {}) {
        addMemberImport(MemberName(packageName = packageName.orEmpty(), simpleName = name), block)
    }

    fun memberImport(packageName: String?, names: Iterable<String>) {
        names.forEach { memberImport(packageName, it) }
    }

    fun memberImport(packageName: String?, vararg names: String) {
        memberImport(packageName, names.asIterable())
    }

    fun memberImport(enum: Enum<*>) {
        builder.addImport(enum)
    }

    fun defaultImport(packageName: String) {
        builder.addDefaultPackageImport(packageName)
    }

    private fun addMemberImport(name: MemberName, block: ImportScope.Block = {}) {
        require(name.simpleName != "*") { "Wildcard imports are not allowed" }
        val alias = ImportScope().apply(block).alias
        if (alias != null) {
            builder.addAliasedImport(name, alias)
        } else {
            builder.addImport(name)
        }
    }

    inner class ImportScope internal constructor() : PoetesseKotlinScope {

        override val config: Poetesse.Config = this@KotlinFileScope.config

        internal typealias Block = ImportScope.() -> Unit

        internal var alias: String? = null

        fun alias(name: String) {
            alias = name
        }
    }

    internal fun build(): PoetesseKotlinFile {
        val file = builder.apply {
            indent(config.indent)
            config.comment?.let { addFileComment(it) }
            if (config.skipLangDefaultImports) {
                addKotlinDefaultImports(includeJvm = true, includeJs = false)
                defaultImport("kotlin.math")
                defaultImport("kotlin.jvm")
            }
        }.build()
        return PoetesseKotlinFile(file)
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(packageName: String?, fileName: String) =
            KotlinFileScope(poetesse.config, packageName, fileName, KPFile.builder(packageName.orEmpty(), fileName))
    }
}
