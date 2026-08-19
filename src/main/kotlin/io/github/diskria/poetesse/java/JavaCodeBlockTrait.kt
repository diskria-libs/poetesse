package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

sealed interface JavaCodeBlockTrait : JavaCodeBlockFactory {
    operator fun JavaCodeBlockRef.unaryPlus() {
        statements.forEach { +it }
    }

    operator fun JPCodeBlock.unaryPlus() {
        if (isEmpty) return
        container.append(this)
    }
}

fun JavaCodeBlockTrait.line(block: JavaCodeScope.Block) {
    +code(block).codeBlock
}

fun JavaCodeBlockTrait.variable(name: String, type: XTypeName?, block: JavaVariableScope.Block = {}): String {
    line(JavaVariableScope.of(name, type).apply(block).build())
    return name
}

@JvmName("variableUntyped")
fun JavaCodeBlockTrait.variable(name: String, block: JavaVariableScope.Block = {}) =
    variable(name, type = null, block)

fun JavaCodeBlockTrait.variable(
    name: String, type: KClass<*>, nullable: Boolean = false, block: JavaVariableScope.Block = {}
) = variable(name, xType(type, nullable = nullable), block)

fun JavaCodeBlockTrait.variable(type: KClass<*>, nullable: Boolean = false, block: JavaVariableScope.Block = {}) =
    EagerDelegate { variable(it, type, nullable, block) }

fun JavaCodeBlockTrait.variable(type: XTypeName, block: JavaVariableScope.Block = {}) =
    EagerDelegate { variable(it, type, block) }

@JvmName("variableUntypedDelegate")
fun JavaCodeBlockTrait.variable(block: JavaVariableScope.Block = {}) =
    EagerDelegate { variable(it, type = null, block) }

inline fun <reified T> JavaCodeBlockTrait.variable(
    name: String, nullable: Boolean = true, noinline block: JavaVariableScope.Block = {}
) = variable(name, T::class, nullable, block)

inline fun <reified T : Any> JavaCodeBlockTrait.variable(
    name: String, noinline block: JavaVariableScope.Block = {}
) = variable<T>(name, nullable = false, block)

inline fun <reified T> JavaCodeBlockTrait.variable(
    nullable: Boolean = true, noinline block: JavaVariableScope.Block = {}
) = EagerDelegate { variable<T>(it, nullable, block) }

inline fun <reified T : Any> JavaCodeBlockTrait.variable(noinline block: JavaVariableScope.Block = {}) =
    variable<T>(nullable = false, block)

internal class JavaCodeBlockContainer(val append: (codeBlock: JPCodeBlock) -> Unit)

private val JavaCodeBlockTrait.container: JavaCodeBlockContainer
    get() = when (this) {
        is JavaBodyScope -> codeBlockContainer
        is JavaCodeBlockScope -> codeBlockContainer
        is JavaEmbeddableCodeBlockScope -> codeBlockContainer
    }
