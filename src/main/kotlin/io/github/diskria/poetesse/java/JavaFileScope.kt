package io.github.diskria.poetesse.java

import com.palantir.javapoet.JavaFile
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName

@PoetesseJava
class JavaFileScope private constructor(
    private val packageName: String?,
    val fileName: String,
) : JavaTypeContainerScope.External {

    private val types: MutableList<JPType> = mutableListOf()

    internal val typeContainerInternalScope = JavaTypeContainerScope.Internal.of(
        append = { types += it },
        nestedClassName = { name -> XClassName.of(packageName, name) },
    )

    internal fun build(settings: Poetesse.Settings): JavaPoetesseFile {
        val primaryType = requireNotNull(types.find { it.name() == fileName }) {
            "File '$fileName' cannot be built because primary type was not configured."
        }
        if (types.size > 1) {
            return MultiClassJavaPoetesseFile.mergeFrom(packageName, fileName, types, settings)
        }
        val javaFile = JavaFile.builder(packageName.orEmpty(), primaryType).apply {
            indent(settings.indent)
            settings.comment?.let { addFileComment(it) }
        }.build()
        return SingleClassJavaPoetesseFile(packageName, fileName, javaFile)
    }

    internal companion object {
        fun of(packageName: String?, fileName: String): JavaFileScope =
            JavaFileScope(packageName, fileName)
    }
}
