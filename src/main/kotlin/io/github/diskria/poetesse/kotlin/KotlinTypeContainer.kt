package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpecHolder
import io.github.diskria.poetesse.XClassName

sealed interface KotlinTypeContainer {

    fun type(kind: KPTypeKind, name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName = with(internal) {
        val className = nestedClassName(name)
        specHolderBuilder.addType(KotlinTypeScope.of(kind, name, className).apply(block).build())
        return className
    }

    fun class_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        type(TypeSpec.Kind.CLASS, name, block)

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
        type(TypeSpec.Kind.OBJECT, name, block)

    fun interface_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        type(TypeSpec.Kind.INTERFACE, name, block)

    fun fun_interface_(name: String, block: KotlinTypeScope.() -> Unit = {}): XClassName =
        interface_(name) {
            modifiers(KPModifier.FUN)
            block()
        }
}

internal interface KotlinTypeContainerInternal {

    val specHolderBuilder: TypeSpecHolder.Builder<*>

    fun nestedClassName(name: String): XClassName

    companion object {
        fun of(
            specHolderBuilder: TypeSpecHolder.Builder<*>,
            nestedClassName: (name: String) -> XClassName,
        ): KotlinTypeContainerInternal = object : KotlinTypeContainerInternal {
            override val specHolderBuilder: TypeSpecHolder.Builder<*> = specHolderBuilder
            override fun nestedClassName(name: String): XClassName = nestedClassName(name)
        }
    }
}

private val KotlinTypeContainer.internal: KotlinTypeContainerInternal
    get() = when (this) {
        is KotlinFileScope -> typeContainer
        is KotlinTypeScope -> typeContainer
    }
