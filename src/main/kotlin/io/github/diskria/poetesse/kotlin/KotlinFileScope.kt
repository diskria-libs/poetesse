package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.MemberName
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

    fun import(className: XClassName, memberName: String? = null, block: ImportScope.Block = {}) {
        require(memberName != "*") { "Wildcard imports are not allowed" }
        val alias = ImportScope().apply(block).alias
        val kp = className.interopToKotlin()
        if (alias != null) {
            if (memberName != null) {
                builder.addAliasedImport(kp, memberName, alias)
            } else {
                builder.addAliasedImport(kp, alias)
            }
        } else {
            builder.addRawImport(buildString {
                append(kp.canonicalName)
                memberName?.let { append(".$it") }
            })
        }
    }

    fun import(klass: KClass<*>, memberName: String? = null, block: ImportScope.Block = {}) {
        import(xClass(klass), memberName, block)
    }

    inline fun <reified T : Any> import(memberName: String? = null, noinline block: ImportScope.Block = {}) {
        import(T::class, memberName, block)
    }

    fun import(className: XClassName, memberNames: Iterable<String>) {
        memberNames.forEach { import(className, it) }
    }

    fun import(klass: KClass<*>, memberNames: Iterable<String>) {
        import(xClass(klass), memberNames)
    }

    fun import(enum: Enum<*>) {
        builder.addImport(enum)
    }

    fun import(packageName: String?, memberName: String, block: ImportScope.Block = {}) {
        require(memberName != "*") { "Wildcard imports are not allowed" }
        val alias = ImportScope().apply(block).alias
        val memberName = MemberName(packageName.orEmpty(), memberName)
        if (alias != null) {
            builder.addAliasedImport(memberName, alias)
        } else {
            builder.addImport(memberName)
        }
    }

    fun import(packageName: String?, memberNames: Iterable<String>) {
        memberNames.forEach { import(packageName, it) }
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
                addDefaultPackageImport("kotlin.math")
                addDefaultPackageImport("kotlin.jvm")
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

private fun KPFileBuilder.addRawImport(import: String) {
    addImport(MemberName("", import))
}
