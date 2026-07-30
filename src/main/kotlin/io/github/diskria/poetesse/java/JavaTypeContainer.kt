package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.XClassName

sealed interface JavaTypeContainer : JavaTypeFactory {

    operator fun JavaDeferredType.unaryPlus(): XClassName {
        val className = internal.nestedClassName(name)
        internal.append(build(className))
        return className
    }

    fun type(kind: JPTypeKind, name: String, block: JavaTypeScope.() -> Unit = {}): XClassName =
        +(this as JavaTypeFactory).type(kind, name, block)

    fun class_(name: String, block: JavaTypeScope.() -> Unit = {}): XClassName =
        type(JPTypeKind.CLASS, name, block)

    fun record_(name: String, block: JavaTypeScope.() -> Unit = {}): XClassName =
        type(JPTypeKind.RECORD, name, block)

    fun interface_(name: String, block: JavaTypeScope.() -> Unit = {}): XClassName =
        type(JPTypeKind.INTERFACE, name, block)

    fun enum_(name: String, block: JavaTypeScope.() -> Unit = {}): XClassName =
        type(JPTypeKind.ENUM, name, block)

    fun annotation_(name: String, block: JavaTypeScope.() -> Unit = {}): XClassName =
        type(JPTypeKind.ANNOTATION, name, block)
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

private val JavaTypeContainer.internal: JavaTypeContainerInternal
    get() = when (this) {
        is JavaFileScope -> typeContainer
        is JavaTypeScope -> typeContainer
    }
