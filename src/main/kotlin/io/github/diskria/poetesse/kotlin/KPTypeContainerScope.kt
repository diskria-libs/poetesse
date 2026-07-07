package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.TypeSpecHolder
import io.github.diskria.poetesse.XClassName

internal interface KPTypeContainerScope {
    val typeSpecHolderBuilder: TypeSpecHolder.Builder<*>

    fun addType(kind: KPTypeKind, name: String, className: XClassName, block: KPTypeScope.() -> Unit): XClassName {
        val typeSpec = KPTypeScope.of(kind, name, className).apply(block).build()
        typeSpecHolderBuilder.addType(typeSpec)
        return className
    }
}
