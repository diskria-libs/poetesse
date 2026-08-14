package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.xClass

class JavaFileScope private constructor(
    override val settings: Poetesse.Settings,
    private val packageName: String?,
    val fileName: String,
) : JavaTypeContainer {

    private val types: MutableList<JPType> = mutableListOf()

    internal val typeContainer = JavaTypeContainerInternal.of(
        append = { types += it },
        nestedClassName = { name -> xClass(packageName, name) },
    )

    internal fun build(settings: Poetesse.Settings): JavaFileRef {
        val primaryType = requireNotNull(types.find { it.name() == fileName }) {
            "File '$fileName' cannot be built because primary type was not configured."
        }
        if (types.size > 1) {
            return MultiClassJavaFileRef.mergeFrom(packageName, fileName, types, settings)
        }
        val javaFile = JPFile.builder(packageName.orEmpty(), primaryType).apply {
            indent(settings.indent)
            settings.comment?.let { addFileComment(it) }
        }.build()
        return SingleClassJavaFileRef(packageName, fileName, javaFile)
    }

    internal companion object {
        fun of(settings: Poetesse.Settings, packageName: String?, fileName: String): JavaFileScope =
            JavaFileScope(settings, packageName, fileName)
    }
}
