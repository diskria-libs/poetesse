package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

sealed interface KotlinCodeBlockTrait : KotlinCodeBlockFactory {
    operator fun KotlinCodeBlockRef.unaryPlus() {
        statements.forEach { +it }
    }

    operator fun KPCodeBlock.unaryPlus() {
        if (isEmpty()) return
        container.append(this)
    }
}

fun KotlinCodeBlockTrait.line(block: KotlinCodeScope.Block) {
    +code(block).codeBlock
}

fun KotlinCodeBlockTrait.variable(name: String, type: XTypeName?, block: KotlinVariableScope.Block = {}): String {
    line(KotlinVariableScope.of(name, type).apply(block).build())
    return name
}

@JvmName("variableUntyped")
fun KotlinCodeBlockTrait.variable(name: String, block: KotlinVariableScope.Block = {}) =
    variable(name, type = null, block)

fun KotlinCodeBlockTrait.variable(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinVariableScope.Block = {}
) = variable(name, xType(type, nullable = nullable), block)

fun KotlinCodeBlockTrait.variable(
    type: KClass<*>, nullable: Boolean = false, block: KotlinVariableScope.Block = {}
) = EagerDelegate { variable(it, type, nullable, block) }

fun KotlinCodeBlockTrait.variable(type: XTypeName, block: KotlinVariableScope.Block = {}) =
    EagerDelegate { variable(it, type, block) }

@JvmName("variableUntypedDelegate")
fun KotlinCodeBlockTrait.variable(block: KotlinVariableScope.Block = {}) =
    EagerDelegate { variable(it, type = null, block) }

inline fun <reified T> KotlinCodeBlockTrait.variable(
    name: String, nullable: Boolean = true, noinline block: KotlinVariableScope.Block = {}
) = variable(name, T::class, nullable, block)

inline fun <reified T : Any> KotlinCodeBlockTrait.variable(
    name: String, noinline block: KotlinVariableScope.Block = {}
) = variable<T>(name, nullable = false, block)

inline fun <reified T> KotlinCodeBlockTrait.variable(
    nullable: Boolean = true, noinline block: KotlinVariableScope.Block = {}
) = EagerDelegate { variable<T>(it, nullable, block) }

inline fun <reified T : Any> KotlinCodeBlockTrait.variable(noinline block: KotlinVariableScope.Block = {}) =
    variable<T>(nullable = false, block)

internal class KotlinCodeBlockContainer(val append: (codeBlock: KPCodeBlock) -> Unit)

private val KotlinCodeBlockTrait.container: KotlinCodeBlockContainer
    get() = when (this) {
        is KotlinBodyScope -> codeBlockContainer
        is KotlinCodeBlockScope -> codeBlockContainer
        is KotlinEmbeddableCodeBlockScope -> codeBlockContainer
    }
