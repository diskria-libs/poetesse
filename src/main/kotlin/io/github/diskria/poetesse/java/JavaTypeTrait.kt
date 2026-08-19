package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XClassName

sealed interface JavaTypeTrait : JavaTypeFactory {
    operator fun JavaTypeRef.unaryPlus(): XClassName {
        val className = container.className(name)
        container.append(build(className))
        return className
    }
}

fun JavaTypeTrait.type(kind: JPTypeKind, name: String, block: JavaTypeScope.Block = {}) =
    +factory.type(kind, name, block)

fun JavaTypeTrait.class_(name: String, block: JavaTypeScope.Block = {}) =
    +factory.class_(name, block)

fun JavaTypeTrait.record_(name: String, block: JavaTypeScope.Block = {}) =
    +factory.record_(name, block)

fun JavaTypeTrait.interface_(name: String, block: JavaTypeScope.Block = {}) =
    +factory.interface_(name, block)

fun JavaTypeTrait.enum_(name: String, block: JavaTypeScope.Block = {}) =
    +factory.enum_(name, block)

fun JavaTypeTrait.annotation_(name: String, block: JavaTypeScope.Block = {}) =
    +factory.annotation_(name, block)

internal class JavaTypeContainer(
    val className: (name: String) -> XClassName,
    val append: (type: JPType) -> Unit,
)

@PublishedApi
internal val JavaTypeTrait.factory: JavaTypeFactory
    get() = this as JavaTypeFactory

private val JavaTypeTrait.container: JavaTypeContainer
    get() = when (this) {
        is JavaFileScope -> typeContainer
        is JavaTypeScope -> typeContainer
    }
