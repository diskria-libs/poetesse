package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import kotlin.reflect.KClass

sealed interface JavaCodeBlockContainer : JavaCodeBlockFactory {

    operator fun JavaCodeBlockRef.unaryPlus() {
        codeBlocks.forEach { +it }
    }

    fun line(build: JavaCodeBuilder) {
        +code(build).codeBlock
    }

    private operator fun JPCodeBlock.unaryPlus() {
        internal.append(this)
    }
}

fun JavaCodeBlockContainer.variable(name: String, mutable: Boolean = false, value: JavaCodeBuilder): String =
    variable(name, { L("var") }, mutable, value)

fun JavaCodeBlockContainer.variable(
    name: String,
    type: XTypeName,
    mutable: Boolean = false,
    interop: Boolean = true,
    value: JavaCodeBuilder
): String = variable(name, { T(type, interop) }, mutable, value)

fun JavaCodeBlockContainer.variable(
    type: XTypeName,
    mutable: Boolean = false,
    interop: Boolean = true,
    value: JavaCodeBuilder
) = EagerDelegate { name -> variable(name, type, mutable, interop, value) }

fun JavaCodeBlockContainer.variable(
    name: String,
    type: KClass<out Any>,
    mutable: Boolean = false,
    nullable: Boolean = false,
    interop: Boolean = true,
    value: JavaCodeBuilder
): String = variable(name, XTypeName.of(type, nullable), mutable, interop, value)

fun JavaCodeBlockContainer.variable(
    type: KClass<out Any>,
    mutable: Boolean = false,
    nullable: Boolean = false,
    interop: Boolean = true,
    value: JavaCodeBuilder
) = EagerDelegate { name -> variable(name, type, mutable, nullable, interop, value) }

inline fun <reified T : Any> JavaCodeBlockContainer.variable(
    name: String,
    mutable: Boolean = false,
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline value: JavaCodeBuilder
): String = variable(name, T::class, mutable, nullable, interop, value)

inline fun <reified T : Any> JavaCodeBlockContainer.variable(
    mutable: Boolean = false,
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline value: JavaCodeBuilder
) = EagerDelegate { name -> variable<T>(name, mutable, nullable, interop, value) }

fun JavaCodeBlockContainer.variable(mutable: Boolean = false, value: JavaCodeBuilder) =
    EagerDelegate { name -> variable(name, mutable, value) }

private fun JavaCodeBlockContainer.variable(
    name: String,
    type: JavaCodeBuilder,
    mutable: Boolean = false,
    value: JavaCodeBuilder
): String {
    line { (if (mutable) "" else "final ") + "${L(type)} $name = ${L(value)}" }
    return name
}

internal interface JavaCodeBlockContainerInternal {

    fun append(codeBlock: JPCodeBlock)

    companion object {
        fun of(
            append: (codeBlock: JPCodeBlock) -> Unit,
        ): JavaCodeBlockContainerInternal = object : JavaCodeBlockContainerInternal {
            override fun append(codeBlock: JPCodeBlock) = append(codeBlock)
        }
    }
}

private val JavaCodeBlockContainer.internal: JavaCodeBlockContainerInternal
    get() = when (this) {
        is JavaMethodScope.BodyScope -> codeBlockContainer
        is JavaCodeBlockScope -> codeBlockContainer
    }
