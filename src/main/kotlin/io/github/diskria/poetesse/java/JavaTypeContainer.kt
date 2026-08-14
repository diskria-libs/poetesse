package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XClassName

sealed interface JavaTypeContainer : JavaTypeFactory {

    operator fun JavaTypeRef.unaryPlus(): XClassName {
        val className = internal.nestedClassName(name)
        internal.append(build(className))
        return className
    }

    fun type(kind: JPTypeKind, name: String, block: JavaTypeScope.Block = {}) =
        +factory.type(kind, name, block)

    fun class_(name: String, block: JavaTypeScope.Block = {}) =
        +factory.class_(name, block)

    fun record_(name: String, block: JavaTypeScope.Block = {}) =
        +factory.record_(name, block)

    fun interface_(name: String, block: JavaTypeScope.Block = {}) =
        +factory.interface_(name, block)

    fun enum_(name: String, block: JavaTypeScope.Block = {}) =
        +factory.enum_(name, block)

    fun annotation_(name: String, block: JavaTypeScope.Block = {}) =
        +factory.annotation_(name, block)
}

internal interface JavaTypeContainerInternal {

    fun append(typeSpec: JPType)
    fun nestedClassName(name: String): XClassName

    companion object {
        fun of(
            append: (type: JPType) -> Unit,
            nestedClassName: (name: String) -> XClassName,
        ): JavaTypeContainerInternal = object : JavaTypeContainerInternal {
            override fun append(typeSpec: JPType) = append(typeSpec)
            override fun nestedClassName(name: String): XClassName = nestedClassName(name)
        }
    }
}

private val JavaTypeContainer.factory: JavaTypeFactory
    get() = this as JavaTypeFactory

private val JavaTypeContainer.internal: JavaTypeContainerInternal
    get() = when (this) {
        is JavaFileScope -> typeContainer
        is JavaTypeScope -> typeContainer
    }
