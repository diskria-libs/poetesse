package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName

sealed interface KotlinTypeTrait : KotlinTypeFactory {
    operator fun KotlinTypeRef.unaryPlus(): XClassName {
        val className = container.classNameFactory(name)
        container.append(build(className))
        return className
    }
}

fun KotlinTypeTrait.type(kind: KPTypeKind, name: String, block: KotlinTypeScope.Block = {}) =
    +factory.type(kind, name, block)

fun KotlinTypeTrait.class_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.class_(name, block)

fun KotlinTypeTrait.value_class_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.value_class_(name, block)

fun KotlinTypeTrait.enum_class_(name: String, block: KotlinEnumTypeScope.Block = {}) =
    +factory.enum_class_(name, block)

fun KotlinTypeTrait.data_class_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.data_class_(name, block)

fun KotlinTypeTrait.annotation_class_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.annotation_class_(name, block)

fun KotlinTypeTrait.expect_class_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.expect_class_(name, block)

fun KotlinTypeTrait.object_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.object_(name, block)

fun KotlinTypeTrait.interface_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.interface_(name, block)

fun KotlinTypeTrait.fun_interface_(name: String, block: KotlinTypeScope.Block = {}) =
    +factory.fun_interface_(name, block)

internal class KotlinTypeContainer(
    val classNameFactory: XClassName.Factory,
    val append: (type: KPType) -> Unit,
)

@PublishedApi
internal val KotlinTypeTrait.factory: KotlinTypeFactory
    get() = this as KotlinTypeFactory

private val KotlinTypeTrait.container: KotlinTypeContainer
    get() = when (this) {
        is KotlinFileScope -> typeContainer
        is AbstractKotlinBodyScope -> typeContainer
    }
