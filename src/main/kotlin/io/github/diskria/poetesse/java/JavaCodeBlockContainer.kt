package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
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

fun JavaCodeBlockContainer.variable(
    name: String, type: XTypeName?, block: JavaVariableScope.() -> Unit
): String {
    line(JavaVariableScope(name, type).apply(block).build())
    return name
}

@JvmName("variableUntyped")
fun JavaCodeBlockContainer.variable(name: String, block: JavaVariableScope.() -> Unit) =
    variable(name, type = null, block)

fun JavaCodeBlockContainer.variable(
    name: String, type: KClass<*>, nullable: Boolean = false, block: JavaVariableScope.() -> Unit
) = variable(name, type.xType(nullable = nullable), block)

fun JavaCodeBlockContainer.variable(
    type: KClass<*>, nullable: Boolean = false, block: JavaVariableScope.() -> Unit
) = EagerDelegate { name -> variable(name, type, nullable, block) }

fun JavaCodeBlockContainer.variable(type: XTypeName, block: JavaVariableScope.() -> Unit) =
    EagerDelegate { name -> variable(name, type, block) }

@JvmName("variableUntypedDelegate")
fun JavaCodeBlockContainer.variable(block: JavaVariableScope.() -> Unit) =
    EagerDelegate { name -> variable(name, type = null, block) }

inline fun <reified T> JavaCodeBlockContainer.variable(
    name: String, nullable: Boolean = true, noinline block: JavaVariableScope.() -> Unit
) = variable(name, T::class, nullable, block)

inline fun <reified T : Any> JavaCodeBlockContainer.variable(
    name: String, noinline block: JavaVariableScope.() -> Unit
) = variable<T>(name, nullable = false, block)

inline fun <reified T> JavaCodeBlockContainer.variable(
    nullable: Boolean = true, noinline block: JavaVariableScope.() -> Unit
) = EagerDelegate { name -> variable<T>(name, nullable, block) }

inline fun <reified T : Any> JavaCodeBlockContainer.variable(noinline block: JavaVariableScope.() -> Unit) =
    variable<T>(nullable = false, block)

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
