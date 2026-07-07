package io.github.diskria.poetesse.java

import com.palantir.javapoet.JavaFile
import com.palantir.javapoet.TypeSpec
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.PoetesseSettings
import io.github.diskria.poetesse.XClassName

@PoetesseJava
class JPFileScope private constructor(
    private val packageName: String?,
    val fileName: String,
): JPTypeContainerScope {

    private val typeSpecs: MutableList<TypeSpec> = mutableListOf()

    fun type(kind: JPTypeKind, name: String, block: JPTypeScope.() -> Unit = {}): XClassName =
        addType(kind, name, className = XClassName.of(packageName.orEmpty(), name), block)

    override fun addType(typeSpec: TypeSpec) {
        typeSpecs += typeSpec
    }

    internal fun build(settings: PoetesseSettings): JPFile {
        val primaryTypeSpec = requireNotNull(typeSpecs.find { it.name() == fileName }) {
            "File '$fileName' cannot be built because primary type was not configured."
        }
        if (typeSpecs.size > 1) {
            return MultiClassJPFile.mergeFrom(packageName, fileName, typeSpecs, settings)
        }
        val javaFile = JavaFile.builder(packageName.orEmpty(), primaryTypeSpec).apply {
            indent(settings.indent)
            settings.comment?.let { addFileComment(it) }
        }.build()
        return SingleClassJPFile(packageName, fileName, javaFile)
    }

    internal companion object {
        fun of(packageName: String?, fileName: String): JPFileScope =
            JPFileScope(packageName, fileName)
    }
}
