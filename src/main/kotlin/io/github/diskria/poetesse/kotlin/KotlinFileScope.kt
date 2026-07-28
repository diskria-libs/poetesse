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
) : KotlinTypeContainerScope.External,
    KotlinFunctionContainerScope.External {

    internal val typeContainerInternalScope = KotlinTypeContainerScope.Internal.of(
        specHolderBuilder = specBuilder,
        nestedClassName = { name -> XClassName.of(packageName, name) },
    )
    internal val functionContainerInternalScope = KotlinFunctionContainerScope.Internal.of(
        specHolderBuilder = specBuilder
    )

    internal fun build(settings: Poetesse.Settings): KotlinDeferredFile {
        val file = specBuilder.apply {
            indent(settings.indent)
            settings.comment?.let { addFileComment(it) }
        }.build()
        return KotlinDeferredFile(file)
    }

    internal companion object {
        fun of(packageName: String?, fileName: String): KotlinFileScope =
            KotlinFileScope(packageName, fileName, KPFile.builder(packageName.orEmpty(), fileName))
    }
}
