package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XClassName

sealed interface JavaTypeContainer : JavaTypeFactory {
    operator fun JavaTypeRef.unaryPlus(): XClassName {
        val className = internal.nestedClassName(name)
        internal.append(build(className))
        return className
    }
}

fun JavaTypeContainer.type(kind: JPTypeKind, name: String, block: JavaTypeScope.Block = {}) =
    +factory.type(kind, name, block)

fun JavaTypeContainer.class_(name: String, block: JavaTypeScope.Block = {}) =
    +factory.class_(name, block)

fun JavaTypeContainer.record_(name: String, block: JavaTypeScope.Block = {}) =
    +factory.record_(name, block)

fun JavaTypeContainer.interface_(name: String, block: JavaTypeScope.Block = {}) =
    +factory.interface_(name, block)

fun JavaTypeContainer.enum_(name: String, block: JavaTypeScope.Block = {}) =
    +factory.enum_(name, block)

fun JavaTypeContainer.annotation_(name: String, block: JavaTypeScope.Block = {}) =
    +factory.annotation_(name, block)

internal class JavaTypeContainerInternal(
    val append: (type: JPType) -> Unit,
    val nestedClassName: (name: String) -> XClassName,
)

@PublishedApi
internal val JavaTypeContainer.factory: JavaTypeFactory
    get() = this as JavaTypeFactory

private val JavaTypeContainer.internal: JavaTypeContainerInternal
    get() = when (this) {
        is JavaFileScope -> typeContainer
        is JavaTypeScope -> typeContainer
    }
