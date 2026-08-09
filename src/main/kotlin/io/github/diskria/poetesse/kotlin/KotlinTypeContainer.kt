package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName

sealed interface KotlinTypeContainer : KotlinTypeFactory {

    operator fun KotlinTypeRef.unaryPlus(): XClassName {
        val className = internal.nestedClassName(name)
        internal.append(build(className))
        return className
    }

    fun type(kind: KPTypeKind, name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        +factory.type(kind, name, block)

    fun class_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.CLASS, name, block)

    fun value_class_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        class_(name) {
            modifiers(KPModifier.VALUE)
            block()
        }

    fun enum_class_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        class_(name) {
            modifiers(KPModifier.ENUM)
            block()
        }

    fun data_class_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        class_(name) {
            modifiers(KPModifier.DATA)
            block()
        }

    fun annotation_class_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        class_(name) {
            modifiers(KPModifier.ANNOTATION)
            block()
        }

    fun object_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.OBJECT, name, block)

    fun interface_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        type(KPTypeKind.INTERFACE, name, block)

    fun fun_interface_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        interface_(name) {
            modifiers(KPModifier.FUN)
            block()
        }
}

internal interface KotlinTypeContainerInternal {

    fun append(type: KPType)
    fun nestedClassName(name: String): XClassName

    companion object {
        fun of(
            append: (KPType) -> Unit,
            nestedClassName: (name: String) -> XClassName,
        ): KotlinTypeContainerInternal = object : KotlinTypeContainerInternal {
            override fun append(type: KPType) = append(type)
            override fun nestedClassName(name: String): XClassName = nestedClassName(name)
        }
    }
}

private val KotlinTypeContainer.factory: KotlinTypeFactory
    get() = this as KotlinTypeFactory

private val KotlinTypeContainer.internal: KotlinTypeContainerInternal
    get() = when (this) {
        is KotlinFileScope -> typeContainer
        is KotlinTypeScope -> typeContainer
    }
