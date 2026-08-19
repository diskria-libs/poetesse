package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.XTypeName

sealed interface KotlinTypeAliasTrait : KotlinTypeAliasFactory {
    operator fun KotlinTypeAliasRef.unaryPlus(): XClassName {
        container.append(spec)
        return container.className(name)
    }
}

fun KotlinTypeAliasTrait.typeAlias(name: String, type: XTypeName, block: KotlinTypeAliasScope.Block = {}) =
    +factory.typeAlias(name, type, block)

internal class KotlinTypeAliasContainer(
    val className: (name: String) -> XClassName,
    val append: (typeAlias: KPTypeAlias) -> Unit,
)

@PublishedApi
internal val KotlinTypeAliasTrait.factory: KotlinTypeAliasFactory
    get() = this as KotlinTypeAliasFactory

private val KotlinTypeAliasTrait.container: KotlinTypeAliasContainer
    get() = when (this) {
        is KotlinFileScope -> typeAliasContainer
        is KotlinTypeScope -> typeAliasContainer
    }
