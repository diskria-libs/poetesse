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
    private val types: MutableList<JPType> = mutableListOf()

    fun import(className: XClassName, memberName: String? = null) {
        require(memberName != "*") { "Wildcard imports are not allowed" }
        val qualifiedName = className.interopToJava(resolveNullability = false).qualifiedName
        if (memberName != null) {
            extraStaticImports += "$qualifiedName.$memberName"
        } else {
            extraImports += qualifiedName
        }
    }

    fun import(klass: KClass<*>, memberName: String? = null) {
        import(xClass(klass), memberName)
    }

    inline fun <reified T : Any> import(memberName: String? = null) {
        import(T::class, memberName)
    }

    fun import(className: XClassName, memberNames: Iterable<String>) {
        memberNames.forEach { import(className, it) }
    }

    fun import(klass: KClass<*>, memberNames: Iterable<String>) {
        import(xClass(klass), memberNames)
    }

    fun import(enum: Enum<*>) {
        extraStaticImports += "${enum.declaringJavaClass.canonicalName}.${enum.name}"
    }

    fun import(packageName: String?, className: String) {
        require(className != "*") { "Wildcard imports are not allowed" }
        extraImports += listOfNotNull(packageName?.ifEmpty { null }, className).joinToString(".")
    }

    fun import(packageName: String?, classNames: Iterable<String>) {
        classNames.forEach { import(packageName, it) }
    }

    internal fun build(): PoetesseJavaFile {
        requireNotNull(types.find { it.name() == fileName }) {
            "File '$fileName' cannot be built because primary type was not configured."
        }
        return PoetesseJavaFile.of(packageName, fileName, types, extraImports, extraStaticImports)
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(packageName: String?, fileName: String) =
            JavaFileScope(poetesse.config, packageName?.ifEmpty { null }, fileName)
    }
}
