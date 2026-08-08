package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXTypeName
import io.github.diskria.poetesse.interop.setNullable
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
    block: JavaVariableScope.() -> Unit
) = fullVariable(name, { T(type) }, block)

fun JavaCodeBlockContainer.variable(
    name: String,
    type: XTypeName,
    initializer: JavaCodeBuilder
) = fullVariable(name, type) { initializer(initializer) }

fun JavaCodeBlockContainer.fullVariable(
    type: XTypeName,
    block: JavaVariableScope.() -> Unit
) = EagerDelegate { name -> fullVariable(name, type, block) }

fun JavaCodeBlockContainer.variable(
    type: XTypeName,
    initializer: JavaCodeBuilder
) = fullVariable(type) { initializer(initializer) }

fun JavaCodeBlockContainer.fullVariable(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    block: JavaVariableScope.() -> Unit
) = fullVariable(name, type.asXTypeName().setNullable(nullable), block)

fun JavaCodeBlockContainer.variable(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    initializer: JavaCodeBuilder
) = fullVariable(name, type, nullable) { initializer(initializer) }

fun JavaCodeBlockContainer.fullVariable(
    type: KClass<out Any>,
    nullable: Boolean = false,
    block: JavaVariableScope.() -> Unit
) = EagerDelegate { name -> fullVariable(name, type, nullable, block) }

fun JavaCodeBlockContainer.variable(
    type: KClass<out Any>,
    nullable: Boolean = false,
    initializer: JavaCodeBuilder
) = fullVariable(type, nullable) { initializer(initializer) }

inline fun <reified T : Any> JavaCodeBlockContainer.fullVariable(
    name: String,
    nullable: Boolean = false,
    noinline block: JavaVariableScope.() -> Unit
) = fullVariable(name, T::class, nullable, block)

inline fun <reified T : Any> JavaCodeBlockContainer.variable(
    name: String,
    nullable: Boolean = false,
    noinline initializer: JavaCodeBuilder
) = fullVariable<T>(name, nullable) { initializer(initializer) }

inline fun <reified T : Any> JavaCodeBlockContainer.fullVariable(
    nullable: Boolean = false,
    noinline block: JavaVariableScope.() -> Unit
) = EagerDelegate { name -> fullVariable<T>(name, nullable, block) }

inline fun <reified T : Any> JavaCodeBlockContainer.variable(
    nullable: Boolean = false,
    noinline initializer: JavaCodeBuilder,
) = fullVariable<T>(nullable) { initializer(initializer) }

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
