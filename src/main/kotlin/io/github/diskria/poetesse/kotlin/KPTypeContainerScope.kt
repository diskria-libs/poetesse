package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.TypeSpecHolder
import io.github.diskria.poetesse.XClassName

sealed interface KPTypeContainerScope {

    fun type(kind: KPTypeKind, name: String, block: KPTypeScope.() -> Unit = {}): XClassName {
        val internalScope = when (this) {
            is KPFileScope -> typeContainerInternalScope
            is KPTypeScope -> typeContainerInternalScope
        }
        val className = internalScope.innerClassName(name)
        val typeSpec = KPTypeScope.of(kind, name, className).apply(block).build()
        internalScope.specHolderBuilder.addType(typeSpec)
        return className
    }

    fun `class`(name: String, block: KPTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.CLASS, name, block)

    fun `expect class`(name: String, block: KPTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.EXPECT_CLASS, name, block)

    fun `value class`(name: String, block: KPTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.VALUE_CLASS, name, block)

    fun `object`(name: String, block: KPTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.OBJECT, name, block)

    fun `interface`(name: String, block: KPTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.INTERFACE, name, block)

    fun `fun interface`(name: String, block: KPTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.FUN_INTERFACE, name, block)

    fun `enum class`(name: String, block: KPTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.ENUM_CLASS, name, block)

    fun `annotation`(name: String, block: KPTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.ANNOTATION, name, block)

    companion object {
        internal interface Internal {
            val specHolderBuilder: TypeSpecHolder.Builder<*>

            fun innerClassName(name: String): XClassName
        }
    }
}
