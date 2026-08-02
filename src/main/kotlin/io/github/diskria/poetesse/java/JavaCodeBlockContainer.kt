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

fun JavaCodeBlockContainer.variable(
    name: String,
    build: JavaVariableBuilder
): String = variable(name, type = { L("var") }, build)

fun JavaCodeBlockContainer.variable(
    name: String,
    type: XTypeName,
    interop: Boolean = true,
    build: JavaVariableBuilder
): String = variable(name, { T(type, interop) }, build)

fun JavaCodeBlockContainer.variable(
    type: XTypeName,
    interop: Boolean = true,
    build: JavaVariableBuilder
) = EagerDelegate { name -> variable(name, type, interop, build) }

fun JavaCodeBlockContainer.variable(
    name: String,
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    build: JavaVariableBuilder
): String = variable(name, XTypeName.of(type, nullable), interop, build)

fun JavaCodeBlockContainer.variable(
    type: KClass<out Any>,
    nullable: Boolean = false,
    interop: Boolean = true,
    build: JavaVariableBuilder
) = EagerDelegate { name -> variable(name, type, nullable, interop, build) }

inline fun <reified T : Any> JavaCodeBlockContainer.variable(
    name: String,
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline build: JavaVariableBuilder
): String = variable(name, T::class, nullable, interop, build)

inline fun <reified T : Any> JavaCodeBlockContainer.variable(
    nullable: Boolean = false,
    interop: Boolean = true,
    noinline build: JavaVariableBuilder
) = EagerDelegate { name -> variable<T>(name, nullable, interop, build) }

fun JavaCodeBlockContainer.variable(build: JavaVariableBuilder) =
    EagerDelegate { name -> variable(name, build) }

private fun JavaCodeBlockContainer.variable(name: String, type: JavaCodeBuilder, build: JavaVariableBuilder): String {
    line { L(JavaVariableScope.of(name, type, build)) }
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
