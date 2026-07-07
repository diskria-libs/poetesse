package io.github.diskria.poetesse.java

import com.palantir.javapoet.TypeSpec
import io.github.diskria.poetesse.XClassName

internal interface JPTypeContainerScope {
    fun addType(typeSpec: TypeSpec)

    fun addType(kind: JPTypeKind, name: String, className: XClassName, block: JPTypeScope.() -> Unit): XClassName {
        val typeSpec = JPTypeScope.of(kind, name, className).apply(block).build()
        addType(typeSpec)
        return className
    }
}
