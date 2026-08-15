package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.XTypeName

sealed interface KotlinTypeContainer : KotlinTypeFactory {
    operator fun KotlinTypeRef.unaryPlus(): XClassName {
        val className = internal.nestedClassName(name)
        internal.appendType(build(className))
        return className
    }

    operator fun KotlinTypeAliasRef.unaryPlus(): XClassName {
        internal.appendTypeAlias(spec)
        return internal.nestedClassName(name)
    }
}

fun KotlinTypeContainer.typeAlias(name: String, type: XTypeName, block: KotlinTypeAliasScope.Block = {}) =
    +factory.typeAlias(name, type, block)

fun KotlinTypeContainer.type(kind: KPTypeKind, name: String, block: KotlinTypeScope.Block = {}) =
    +factory.type(kind, name, block)

fun KotlinTypeContainer.class_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.class_(name, block)

fun KotlinTypeContainer.value_class_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.value_class_(name, block)

fun KotlinTypeContainer.enum_class_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.enum_class_(name, block)

fun KotlinTypeContainer.data_class_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.data_class_(name, block)

fun KotlinTypeContainer.annotation_class_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.annotation_class_(name, block)

fun KotlinTypeContainer.object_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.object_(name, block)

fun KotlinTypeContainer.interface_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.interface_(name, block)

fun KotlinTypeContainer.fun_interface_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.fun_interface_(name, block)

internal interface KotlinTypeContainerInternal {

    fun appendType(type: KPType)
    fun appendTypeAlias(typeAlias: KPTypeAlias)
    fun nestedClassName(name: String): XClassName

    companion object {
        fun of(
            appendType: (type: KPType) -> Unit,
            appendTypeAlias: (typeAlias: KPTypeAlias) -> Unit,
            nestedClassName: (name: String) -> XClassName,
        ): KotlinTypeContainerInternal = object : KotlinTypeContainerInternal {
            override fun appendType(type: KPType) = appendType(type)
            override fun appendTypeAlias(typeAlias: KPTypeAlias) = appendTypeAlias(typeAlias)
            override fun nestedClassName(name: String): XClassName = nestedClassName(name)
        }
    }
}

@PublishedApi
internal val KotlinTypeContainer.factory: KotlinTypeFactory
    get() = this as KotlinTypeFactory

private val KotlinTypeContainer.internal: KotlinTypeContainerInternal
    get() = when (this) {
        is KotlinFileScope -> typeContainer
        is KotlinTypeScope -> typeContainer
    }
