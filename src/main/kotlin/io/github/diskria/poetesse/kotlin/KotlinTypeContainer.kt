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

    fun typeAlias(
        name: String,
        type: XTypeName<*, *>,
        block: KotlinTypeAliasScope.() -> Unit = {}
    ) = +factory.typeAlias(name, type, block)

    fun type(kind: KPTypeKind, name: String, block: KotlinTypeScope.(className: XClassName) -> Unit = {}): XClassName =
        +factory.type(kind, name, block)

    fun class_(name: String, block: KotlinTypeScope.(className: XClassName) -> Unit = {}): XClassName =
        +factory.class_(name, block)

    fun value_class_(name: String, block: KotlinTypeScope.(className: XClassName) -> Unit = {}): XClassName =
        +factory.value_class_(name, block)

    fun enum_class_(name: String, block: KotlinTypeScope.(className: XClassName) -> Unit = {}): XClassName =
        +factory.enum_class_(name, block)

    fun data_class_(name: String, block: KotlinTypeScope.(className: XClassName) -> Unit = {}): XClassName =
        +factory.data_class_(name, block)

    fun annotation_class_(name: String, block: KotlinTypeScope.(className: XClassName) -> Unit = {}): XClassName =
        +factory.annotation_class_(name, block)

    fun object_(name: String, block: KotlinTypeScope.(className: XClassName) -> Unit = {}): XClassName =
        +factory.object_(name, block)

    fun interface_(name: String, block: KotlinTypeScope.(className: XClassName) -> Unit = {}): XClassName =
        +factory.interface_(name, block)

    fun fun_interface_(name: String, block: KotlinTypeScope.(className: XClassName) -> Unit = {}): XClassName =
        +factory.fun_interface_(name, block)
}

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

private val KotlinTypeContainer.factory: KotlinTypeFactory
    get() = this as KotlinTypeFactory

private val KotlinTypeContainer.internal: KotlinTypeContainerInternal
    get() = when (this) {
        is KotlinFileScope -> typeContainer
        is KotlinTypeScope -> typeContainer
    }
