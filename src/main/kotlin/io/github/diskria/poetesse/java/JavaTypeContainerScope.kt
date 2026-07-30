package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.XClassName

class JavaTypeContainerScope private constructor() {

    sealed interface External : JavaTypeFactory {

        fun type(kind: JPTypeKind, name: String, block: JavaTypeScope.() -> Unit = {}): XClassName = with(internal) {
            val className = nestedClassName(name)
            append((this@External as JavaTypeFactory).type(kind, name, block).build(className))
            return className
        }

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

        operator fun JavaDeferredType.unaryPlus(): XClassName {
            val className = internal.nestedClassName(name)
            internal.append(build(className))
            return className
        }
    }

    internal interface Internal {

        fun append(typeSpec: JPType)
        fun nestedClassName(name: String): XClassName

        companion object {
            fun of(
                append: (type: JPType) -> Unit,
                nestedClassName: (name: String) -> XClassName,
            ): Internal = object : Internal {
                override fun append(typeSpec: JPType) = append(typeSpec)
                override fun nestedClassName(name: String): XClassName = nestedClassName(name)
            }
        }
    }
}

private val JavaTypeContainerScope.External.internal: JavaTypeContainerScope.Internal
    get() = when (this) {
        is JavaFileScope -> typeContainerInternalScope
        is JavaTypeScope -> typeContainerInternalScope
    }
