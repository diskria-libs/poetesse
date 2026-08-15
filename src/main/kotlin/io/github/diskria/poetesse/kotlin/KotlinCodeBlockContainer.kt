package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

sealed interface KotlinCodeBlockContainer : KotlinCodeBlockFactory {
    operator fun KotlinCodeBlockRef.unaryPlus() {
        statements.forEach { +it }
    }

    operator fun KPCodeBlock.unaryPlus() {
        if (isEmpty()) return
        internal.append(this)
    }
}

fun KotlinCodeBlockContainer.line(block: KotlinCodeScope.Block) {
    +code(block).codeBlock
}

fun KotlinCodeBlockContainer.variable(name: String, type: XTypeName?, block: KotlinVariableScope.Block = {}): String {
    line(KotlinVariableScope.of(name, type).apply(block).build())
    return name
}

@JvmName("variableUntyped")
fun KotlinCodeBlockContainer.variable(name: String, block: KotlinVariableScope.Block = {}) =
    variable(name, type = null, block)

fun KotlinCodeBlockContainer.variable(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinVariableScope.Block = {}
) = variable(name, xType(type, nullable = nullable), block)

fun KotlinCodeBlockContainer.variable(
    type: KClass<*>, nullable: Boolean = false, block: KotlinVariableScope.Block = {}
) = EagerDelegate { name -> variable(name, type, nullable, block) }

fun KotlinCodeBlockContainer.variable(type: XTypeName, block: KotlinVariableScope.Block = {}) =
    EagerDelegate { name -> variable(name, type, block) }

@JvmName("variableUntypedDelegate")
fun KotlinCodeBlockContainer.variable(block: KotlinVariableScope.Block = {}) =
    EagerDelegate { name -> variable(name, type = null, block) }

inline fun <reified T> KotlinCodeBlockContainer.variable(
    name: String, nullable: Boolean = true, noinline block: KotlinVariableScope.Block = {}
) = variable(name, T::class, nullable, block)

inline fun <reified T : Any> KotlinCodeBlockContainer.variable(
    name: String, noinline block: KotlinVariableScope.Block = {}
) = variable<T>(name, nullable = false, block)

inline fun <reified T> KotlinCodeBlockContainer.variable(
    nullable: Boolean = true, noinline block: KotlinVariableScope.Block = {}
) = EagerDelegate { name -> variable<T>(name, nullable, block) }

inline fun <reified T : Any> KotlinCodeBlockContainer.variable(noinline block: KotlinVariableScope.Block = {}) =
    variable<T>(nullable = false, block)

internal interface KotlinCodeBlockContainerInternal {

    fun append(codeBlock: KPCodeBlock)

    companion object {
        fun of(
            append: (codeBlock: KPCodeBlock) -> Unit,
        ): KotlinCodeBlockContainerInternal = object : KotlinCodeBlockContainerInternal {
            override fun append(codeBlock: KPCodeBlock) = append(codeBlock)
        }
    }
}

private val KotlinCodeBlockContainer.internal: KotlinCodeBlockContainerInternal
    get() = when (this) {
        is KotlinBodyScope -> codeBlockContainer
        is KotlinEmbeddableCodeBlockScope -> codeBlockContainer
        is KotlinCodeBlockScope -> codeBlockContainer
    }
