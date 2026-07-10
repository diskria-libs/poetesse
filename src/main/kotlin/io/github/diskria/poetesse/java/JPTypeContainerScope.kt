package io.github.diskria.poetesse.java

import com.palantir.javapoet.TypeSpec
import io.github.diskria.poetesse.XClassName

sealed interface JPTypeContainerScope {
    val `class`: JPTypeKind get() = JPTypeKind.CLASS
    val record: JPTypeKind get() = JPTypeKind.RECORD
    val `interface`: JPTypeKind get() = JPTypeKind.INTERFACE
    val `enum`: JPTypeKind get() = JPTypeKind.ENUM
    val `annotation`: JPTypeKind get() = JPTypeKind.ANNOTATION

    fun type(kind: JPTypeKind, name: String, block: JPTypeScope.() -> Unit = {}): XClassName {
        val internalScope = when (this) {
            is JPFileScope -> typeContainerInternalScope
            is JPTypeScope -> typeContainerInternalScope
        }
        val className = internalScope.innerClassName(name)
        val typeSpec = JPTypeScope.of(kind, name, className).apply(block).build()
        internalScope.addType(typeSpec)
        return className
    }

    fun `class`(name: String, block: JPTypeScope.() -> Unit = {}): XClassName =
        type(JPTypeKind.CLASS, name, block)

    fun record(name: String, block: JPTypeScope.() -> Unit = {}): XClassName =
        type(JPTypeKind.RECORD, name, block)

    fun `interface`(name: String, block: JPTypeScope.() -> Unit = {}): XClassName =
        type(JPTypeKind.INTERFACE, name, block)

    fun `enum`(name: String, block: JPTypeScope.() -> Unit = {}): XClassName =
        type(JPTypeKind.ENUM, name, block)

    fun `annotation`(name: String, block: JPTypeScope.() -> Unit = {}): XClassName =
        type(JPTypeKind.ANNOTATION, name, block)

    companion object {
        internal interface Internal {
            fun innerClassName(name: String): XClassName
            fun addType(typeSpec: TypeSpec)
        }
    }
}
