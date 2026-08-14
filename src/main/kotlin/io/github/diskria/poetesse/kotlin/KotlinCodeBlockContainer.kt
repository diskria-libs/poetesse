package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

sealed interface KotlinCodeBlockContainer : KotlinCodeBlockFactory {

    operator fun KotlinCodeBlockRef.unaryPlus() {
        statements.forEach { +it }
    }

    fun line(build: KotlinCodeBuilder) {
        +code(build).codeBlock
    }

    private operator fun KPCodeBlock.unaryPlus() {
        if (isEmpty()) return
        internal.append(this)
    }
}

fun KotlinCodeBlockContainer.variable(
    name: String, type: XTypeName<*, *>?, block: KotlinVariableScope.() -> Unit
): String {
    line(KotlinVariableScope(settings, name, type).apply(block).build())
    return name
}

@JvmName("variableUntyped")
fun KotlinCodeBlockContainer.variable(name: String, block: KotlinVariableScope.() -> Unit) =
    variable(name, type = null, block)

fun KotlinCodeBlockContainer.variable(
    name: String, type: KClass<*>, nullable: Boolean = false, block: KotlinVariableScope.() -> Unit
) = variable(name, xType(type, nullable = nullable), block)

fun KotlinCodeBlockContainer.variable(
    type: KClass<*>, nullable: Boolean = false, block: KotlinVariableScope.() -> Unit
) = EagerDelegate { name -> variable(name, type, nullable, block) }

fun KotlinCodeBlockContainer.variable(type: XTypeName<*, *>, block: KotlinVariableScope.() -> Unit) =
    EagerDelegate { name -> variable(name, type, block) }

@JvmName("variableUntypedDelegate")
fun KotlinCodeBlockContainer.variable(block: KotlinVariableScope.() -> Unit) =
    EagerDelegate { name -> variable(name, type = null, block) }

inline fun <reified T> KotlinCodeBlockContainer.variable(
    name: String, nullable: Boolean = true, noinline block: KotlinVariableScope.() -> Unit
) = variable(name, T::class, nullable, block)

inline fun <reified T : Any> KotlinCodeBlockContainer.variable(
    name: String, noinline block: KotlinVariableScope.() -> Unit
) = variable<T>(name, nullable = false, block)

inline fun <reified T> KotlinCodeBlockContainer.variable(
    nullable: Boolean = true, noinline block: KotlinVariableScope.() -> Unit
) = EagerDelegate { name -> variable<T>(name, nullable, block) }

inline fun <reified T : Any> KotlinCodeBlockContainer.variable(noinline block: KotlinVariableScope.() -> Unit) =
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
