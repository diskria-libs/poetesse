package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.nullable

@PoetesseKotlin
class KotlinFileScope private constructor(
    private val packageName: String?,
    val fileName: String,
    private val specBuilder: KPFileBuilder,
) : KotlinTypeContainer,
    KotlinFunctionContainer {

    internal val typeContainer = KotlinTypeContainerInternal.of(
        append = { specBuilder.addType(it) },
        nestedClassName = { name -> XClassName.of(packageName, name).nullable(false) },
    )
    internal val functionContainer = KotlinFunctionContainerInternal.of(
        append = { specBuilder.addFunction(it) }
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
