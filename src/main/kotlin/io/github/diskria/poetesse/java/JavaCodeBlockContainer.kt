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
        if (isEmpty) return
        internal.append(this)
    }
}

fun JavaCodeBlockContainer.fullVariable(name: String, block: JavaVariableScope.() -> Unit) =
    fullVariable(name, type = null, block)

fun JavaCodeBlockContainer.variable(name: String, initializer: JavaCodeBuilder) =
    fullVariable(name) { initializer(initializer) }

fun JavaCodeBlockContainer.fullVariable(
    name: String,
    type: XTypeName,
    interop: Boolean = true,
    block: JavaVariableScope.() -> Unit
) = fullVariable(name, { T(type, interop) }, block)

fun JavaCodeBlockContainer.variable(
    name: String,
    type: XTypeName,
    interop: Boolean = true,
    initializer: JavaCodeBuilder
) = fullVariable(name, type, interop) { initializer(initializer) }

fun JavaCodeBlockContainer.fullVariable(
    type: XTypeName,
    interop: Boolean = true,
    block: JavaVariableScope.() -> Unit
) = EagerDelegate { name -> fullVariable(name, type, interop, block) }

fun JavaCodeBlockContainer.variable(
    type: XTypeName,
    interop: Boolean = true,
    initializer: JavaCodeBuilder
) = fullVariable(type, interop) { initializer(initializer) }

fun JavaCodeBlockContainer.fullVariable(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    block: JavaVariableScope.() -> Unit
) = fullVariable(name, XTypeName.of(type, nullable), interop, block)

fun JavaCodeBlockContainer.variable(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    initializer: JavaCodeBuilder
) = fullVariable(name, type, nullable, interop) { initializer(initializer) }

fun JavaCodeBlockContainer.fullVariable(
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    block: JavaVariableScope.() -> Unit
) = EagerDelegate { name -> fullVariable(name, type, nullable, interop, block) }

fun JavaCodeBlockContainer.variable(
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    initializer: JavaCodeBuilder
) = fullVariable(type, nullable, interop) { initializer(initializer) }

inline fun <reified T : Any> JavaCodeBlockContainer.fullVariable(
    name: String,
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline block: JavaVariableScope.() -> Unit
) = fullVariable(name, T::class, nullable, interop, block)

inline fun <reified T : Any> JavaCodeBlockContainer.variable(
    name: String,
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline initializer: JavaCodeBuilder
) = fullVariable<T>(name, nullable, interop) { initializer(initializer) }

inline fun <reified T : Any> JavaCodeBlockContainer.fullVariable(
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline block: JavaVariableScope.() -> Unit
) = EagerDelegate { name -> fullVariable<T>(name, nullable, interop, block) }

inline fun <reified T : Any> JavaCodeBlockContainer.variable(
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline initializer: JavaCodeBuilder,
) = fullVariable<T>(nullable, interop) { initializer(initializer) }

fun JavaCodeBlockContainer.fullVariable(block: JavaVariableScope.() -> Unit) =
    EagerDelegate { name -> fullVariable(name, block) }

fun JavaCodeBlockContainer.variable(initializer: JavaCodeBuilder) =
    fullVariable { initializer(initializer) }

private fun JavaCodeBlockContainer.fullVariable(
    name: String,
    type: JavaCodeBuilder?,
    block: JavaVariableScope.() -> Unit
): String {
    line(JavaVariableScope(name, type?.let { code(it) }).apply(block).build())
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
        is JavaConstructorScope.BodyScope -> codeBlockContainer
        is JavaMethodScope.BodyScope -> codeBlockContainer
        is JavaCodeBlockScope -> codeBlockContainer
    }
