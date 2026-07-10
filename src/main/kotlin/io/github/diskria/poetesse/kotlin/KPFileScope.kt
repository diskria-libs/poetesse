package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpecHolder
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.XClassName
import io.github.diskria.poetesse.kotlin.KPRootScope.File

@PoetesseKotlin
class KPFileScope private constructor(
    private val packageName: String?,
    val fileName: String,
    private val specBuilder: FileSpec.Builder,
) : KPTypeContainerScope {

    internal val typeContainerInternalScope = object : KPTypeContainerScope.Companion.Internal {
        override val specHolderBuilder: TypeSpecHolder.Builder<*> get() = specBuilder

        override fun innerClassName(name: String): XClassName =
            XClassName.of(packageName, name)
    }

    internal fun build(settings: Poetesse.Settings): KPFile {
        val file = specBuilder.apply {
            indent(settings.indent)
            settings.comment?.let { addFileComment(it) }
        }.build()
        return KPFile(file)
    }

    internal companion object {
        fun of(packageName: String?, fileName: String): KPFileScope =
            KPFileScope(packageName, fileName, File.builder(packageName.orEmpty(), fileName))
    }
}
