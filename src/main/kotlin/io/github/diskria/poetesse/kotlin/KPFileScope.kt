package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpecHolder
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.PoetesseSettings
import io.github.diskria.poetesse.XClassName
import io.github.diskria.poetesse.kotlin.KotlinScope.File

@PoetesseKotlin
class KPFileScope private constructor(
    val fileName: String,
    private val specBuilder: FileSpec.Builder,
) : KPTypeContainerScope {

    override val typeSpecHolderBuilder: TypeSpecHolder.Builder<*> get() = specBuilder

    fun type(kind: KPTypeKind, name: String, block: KPTypeScope.() -> Unit = {}): XClassName =
        addType(kind, name, className = XClassName.of(specBuilder.packageName.takeIf { it.isNotEmpty() }, name), block)

    internal fun build(settings: PoetesseSettings): KPFile {
        val file = specBuilder.apply {
            indent(settings.indent)
            settings.comment?.let { addFileComment(it) }
        }.build()
        return KPFile(file)
    }

    internal companion object {
        fun of(packageName: String?, fileName: String): KPFileScope =
            KPFileScope(fileName, File.builder(packageName.orEmpty(), fileName))
    }
}
