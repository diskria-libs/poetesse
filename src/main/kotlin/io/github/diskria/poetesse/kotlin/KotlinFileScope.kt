package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.xClass

class KotlinFileScope private constructor(
    override val settings: Poetesse.Settings,
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

    internal fun build(settings: Poetesse.Settings): KotlinFileRef {
        val file = specBuilder.apply {
            indent(settings.indent)
            settings.comment?.let { addFileComment(it) }
        }.build()
        return KotlinFileRef(file)
    }

    internal companion object {
        fun of(settings: Poetesse.Settings, packageName: String?, fileName: String) =
            KotlinFileScope(settings, packageName, fileName, KPFile.builder(packageName.orEmpty(), fileName))
    }
}
