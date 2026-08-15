package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope
import io.github.diskria.poetesse.interop.xClass

class JavaFileScope private constructor(
    override val config: Poetesse.Config,
    private val packageName: String?,
    val fileName: String,
) : JavaTypeContainer {

    internal typealias Block = JavaFileScope.() -> Unit

    private val types: MutableList<JPType> = mutableListOf()

    internal val typeContainer = JavaTypeContainerInternal(
        append = { types += it },
        nestedClassName = { name -> xClass(packageName, name) },
    )

    internal fun build(): JavaFileRef {
        val primaryType = requireNotNull(types.find { it.name() == fileName }) {
            "File '$fileName' cannot be built because primary type was not configured."
        }
        if (types.size > 1) {
            return MultiClassJavaFileRef.mergeFrom(packageName, fileName, types)
        }
        val javaFile = JPFile.builder(packageName.orEmpty(), primaryType).apply {
            indent(config.indent)
            config.comment?.let { addFileComment(it) }
        }.build()
        return SingleClassJavaFileRef(packageName, fileName, javaFile)
    }

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(packageName: String?, fileName: String) =
            JavaFileScope(scope.config, packageName, fileName)
    }
}
