package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName

sealed interface KotlinTypeContainer {

    fun type(kind: KPTypeKind, name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName = with(internal) {
        val className = nestedClassName(name)
        holderBuilder.addType(KotlinTypeScope.of(kind, name, className).apply(block).build())
        return className
    }

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

    val holderBuilder: KPTypeHolderBuilder

    fun nestedClassName(name: String): XClassName

    companion object {
        fun of(
            holderBuilder: KPTypeHolderBuilder,
            nestedClassName: (name: String) -> XClassName,
        ): KotlinTypeContainerInternal = object : KotlinTypeContainerInternal {
            override val holderBuilder: KPTypeHolderBuilder = holderBuilder
            override fun nestedClassName(name: String): XClassName = nestedClassName(name)
        }
    }
}

private val KotlinTypeContainer.internal: KotlinTypeContainerInternal
    get() = when (this) {
        is KotlinFileScope -> typeContainer
        is KotlinTypeScope -> typeContainer
    }
