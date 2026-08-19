package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.qualifiedName
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.interopToJava
import io.github.diskria.poetesse.interop.xClass
import kotlin.reflect.KClass

class JavaFileScope private constructor(
    override val config: Poetesse.Config,
    private val packageName: String?,
    val fileName: String,
) : PoetesseJavaScope,
    JavaTypeTrait {

    internal typealias Block = JavaFileScope.() -> Unit

    private val nestedClassNameFactory: (String) -> XClassName = { name -> xClass(packageName, name) }

    internal val typeContainer = JavaTypeContainer(nestedClassNameFactory) { types += it }

    private val extraImports: MutableSet<String> = mutableSetOf()
    private val extraStaticImports: MutableSet<String> = mutableSetOf()
    private val defaultImportPackageNames: MutableSet<String> = mutableSetOf()
    private val types: MutableList<JPType> = mutableListOf()

    fun import(className: XClassName) {
        extraImports += className.interopToJava(resolveNullability = false).qualifiedName
    }

    fun import(klass: KClass<*>) {
        import(xClass(klass))
    }

    inline fun <reified T : Any> import() {
        import(T::class)
    }

    fun import(packageName: String?, name: String) {
        import(XClassName.of(packageName, name.split('.')))
    }

    fun import(packageName: String?, names: Iterable<String>) {
        names.forEach { import(packageName, it) }
    }

    fun import(packageName: String?, vararg names: String) {
        import(packageName, names.asIterable())
    }

    fun memberImport(owner: XClassName, name: String) {
        require(name != "*") { "Wildcard imports are not allowed" }
        extraStaticImports += "${owner.interopToJava(resolveNullability = false).qualifiedName}.$name"
    }

    fun memberImport(className: XClassName, names: Iterable<String>) {
        names.forEach { memberImport(className, it) }
    }

    fun memberImport(className: XClassName, vararg names: String) {
        memberImport(className, names.asIterable())
    }

    fun memberImport(owner: KClass<*>, name: String) {
        memberImport(xClass(owner), name)
    }

    fun memberImport(owner: KClass<*>, names: Iterable<String>) {
        names.forEach { memberImport(owner, it) }
    }

    fun memberImport(owner: KClass<*>, vararg names: String) {
        memberImport(owner, names.asIterable())
    }

    inline fun <reified Owner : Any> memberImport(name: String) {
        memberImport(Owner::class, name)
    }

    inline fun <reified Owner : Any> memberImport(names: Iterable<String>) {
        names.forEach { memberImport<Owner>(it) }
    }

    inline fun <reified Owner : Any> memberImport(vararg names: String) {
        memberImport<Owner>(names.asIterable())
    }

    fun memberImport(packageName: String?, name: String) {
        memberImport(XClassName.of(packageName, name.split('.')))
    }

    fun memberImport(packageName: String?, names: Iterable<String>) {
        names.forEach { memberImport(packageName, it) }
    }

    fun memberImport(packageName: String?, vararg names: String) {
        memberImport(packageName, names.asIterable())
    }

    fun memberImport(enum: Enum<*>) {
        extraStaticImports += "${enum.declaringJavaClass.canonicalName}.${enum.name}"
    }

    fun defaultImport(packageName: String) {
        defaultImportPackageNames += packageName
    }

    internal fun build(): PoetesseJavaFile {
        requireNotNull(types.find { it.name() == fileName }) {
            "File '$fileName' cannot be built because primary type was not configured."
        }
        if (config.skipLangDefaultImports) {
            defaultImport("java.lang")
        }
        return PoetesseJavaFile.of(
            packageName,
            fileName,
            types,
            extraImports,
            extraStaticImports,
            defaultImportPackageNames,
        )
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(packageName: String?, fileName: String) =
            JavaFileScope(poetesse.config, packageName?.ifEmpty { null }, fileName)
    }
}
