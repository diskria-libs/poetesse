package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope
import io.github.diskria.poetesse.interop.xClass

class KotlinFileScope private constructor(
    override val config: Poetesse.Config,
    private val packageName: String?,
    val fileName: String,
    private val specBuilder: KPFileBuilder,
) : KotlinTypeContainer,
    KotlinPropertyContainer,
    KotlinFunctionContainer {

    internal typealias Block = KotlinFileScope.() -> Unit

    internal val typeContainer = KotlinTypeContainerInternal.of(
        appendType = { specBuilder.addType(it) },
        appendTypeAlias = { specBuilder.addTypeAlias(it) },
        nestedClassName = { name -> xClass(packageName, name) },
    )
    internal val propertyContainer = KotlinPropertyContainerInternal.of(
        append = { specBuilder.addProperty(it) }
    )
    internal val functionContainer = KotlinFunctionContainerInternal.of(
        append = { specBuilder.addFunction(it) }
    )

    internal fun build(): KotlinFileRef {
        val file = specBuilder.apply {
            indent(config.indent)
            config.comment?.let { addFileComment(it) }
        }.build()
        return KotlinFileRef(file)
    }

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(packageName: String?, fileName: String) =
            KotlinFileScope(scope.config, packageName, fileName, KPFile.builder(packageName.orEmpty(), fileName))
    }
}
