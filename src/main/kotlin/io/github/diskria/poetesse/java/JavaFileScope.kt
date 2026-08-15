package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.xClass

class JavaFileScope private constructor(
    override val config: Poetesse.Config,
    private val packageName: String?,
    val fileName: String,
) : JavaTypeTrait {

    internal typealias Block = JavaFileScope.() -> Unit

    private val types: MutableList<JPType> = mutableListOf()

    internal val typeContainer = JavaTypeContainer({ xClass(packageName, it) }) { types += it }

    internal fun build(): PoetesseJavaFile {
        val primaryType = requireNotNull(types.find { it.name() == fileName }) {
            "File '$fileName' cannot be built because primary type was not configured."
        }
        if (types.size > 1) {
            return MultiClassJavaFile.mergeFrom(packageName, fileName, types)
        }
        val javaFile = JPFile.builder(packageName.orEmpty(), primaryType).apply {
            indent(config.indent)
            config.comment?.let { addFileComment(it) }
        }.build()
        return SingleClassJavaFile(packageName, fileName, javaFile)
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(packageName: String?, fileName: String) =
            JavaFileScope(poetesse.config, packageName, fileName)
    }
}
