package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.FileSpec
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.XClassName

@PoetesseKotlin
class KotlinFileScope private constructor(
    private val packageName: String?,
    val fileName: String,
    private val specBuilder: FileSpec.Builder,
) : KotlinTypeContainer,
    KotlinFunctionContainer {

    internal val typeContainer = KotlinTypeContainerInternal.of(
        specHolderBuilder = specBuilder,
        nestedClassName = { name -> XClassName.of(packageName, name) },
    )
    internal val functionContainer = KotlinFunctionContainerInternal.of(
        specHolderBuilder = specBuilder
    )

    internal fun build(settings: Poetesse.Settings): KotlinPoetesseFile {
        val file = specBuilder.apply {
            indent(settings.indent)
            settings.comment?.let { addFileComment(it) }
        }.build()
        return KotlinPoetesseFile(file)
    }

    internal companion object {
        fun of(packageName: String?, fileName: String): KotlinFileScope =
            KotlinFileScope(packageName, fileName, KPFile.builder(packageName.orEmpty(), fileName))
    }
}
