package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.XClassName

@PoetesseKotlin
class KotlinFileScope private constructor(
    private val packageName: String?,
    val fileName: String,
    private val specBuilder: KPFileBuilder,
) : KotlinTypeContainer,
    KotlinFunctionContainer {

    internal val typeContainer = KotlinTypeContainerInternal.of(
        holderBuilder = specBuilder,
        nestedClassName = { name -> XClassName.of(packageName, name) },
    )
    internal val functionContainer = KotlinFunctionContainerInternal.of(
        holderBuilder = specBuilder
    )

    internal fun build(settings: Poetesse.Settings): KotlinFileRef {
        val file = specBuilder.apply {
            indent(settings.indent)
            settings.comment?.let { addFileComment(it) }
        }.build()
        return KotlinFileRef(file)
    }

    internal companion object {
        fun of(packageName: String?, fileName: String): KotlinFileScope =
            KotlinFileScope(packageName, fileName, KPFile.builder(packageName.orEmpty(), fileName))
    }
}
