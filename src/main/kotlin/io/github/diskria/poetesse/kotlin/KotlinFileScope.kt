package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.xClass

class KotlinFileScope private constructor(
    override val config: Poetesse.Config,
    private val packageName: String?,
    val fileName: String,
    private val builder: KPFileBuilder,
) : KotlinTypeTrait,
    KotlinTypeAliasTrait,
    KotlinPropertyTrait,
    KotlinFunctionTrait {

    internal typealias Block = KotlinFileScope.() -> Unit

    internal val typeContainer = KotlinTypeContainer({ xClass(packageName, it) }, builder::addType)
    internal val typeAliasContainer = KotlinTypeAliasContainer({ xClass(packageName, it) }, builder::addTypeAlias)
    internal val propertyContainer = KotlinPropertyContainer(builder::addProperty)
    internal val functionContainer = KotlinFunctionContainer(builder::addFunction)

    internal fun build(): PoetesseKotlinFile {
        val file = builder.apply {
            indent(config.indent)
            config.comment?.let { addFileComment(it) }
        }.build()
        return PoetesseKotlinFile(file)
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(packageName: String?, fileName: String) =
            KotlinFileScope(poetesse.config, packageName, fileName, KPFile.builder(packageName.orEmpty(), fileName))
    }
}
