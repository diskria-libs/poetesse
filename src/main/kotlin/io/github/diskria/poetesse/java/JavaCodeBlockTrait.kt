package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XCodeBlockMutationType
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

sealed interface JavaCodeBlockTrait : JavaCodeBlockFactory {
    operator fun JavaCodeBlockRef.unaryPlus() {
        +mutations
    }

    operator fun List<JavaCodeBlockMutation>.unaryPlus() {
        forEach {
            if (it.type == XCodeBlockMutationType.ADD_STATEMENT) {
                +it.codeBlock
            } else {
                container.applyCodeBlockMutation(it.type, it.codeBlock)
            }
        }
    }

    operator fun JPCodeBlock.unaryPlus() {
        if (isEmpty) return
        container.applyCodeBlockMutation(XCodeBlockMutationType.ADD_STATEMENT, this)
    }
}

fun JavaCodeBlockTrait.line(block: JavaCodeScope.Block) {
    +code(block).codeBlock
}

fun JavaCodeBlockTrait.controlFlow(block: JavaControlFlowScope.Block) {
    +JavaControlFlowScope.of().apply(block).build()
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

inline fun <reified T : Any> JavaCodeBlockTrait.variable(name: String, noinline block: JavaVariableScope.Block = {}) =
    variable<T>(name, nullable = false, block)

inline fun <reified T> JavaCodeBlockTrait.variable(
    nullable: Boolean = true, noinline block: JavaVariableScope.Block = {}
) = EagerDelegate { variable<T>(it, nullable, block) }

inline fun <reified T : Any> JavaCodeBlockTrait.variable(noinline block: JavaVariableScope.Block = {}) =
    variable<T>(nullable = false, block)

fun JavaCodeBlockTrait.val_(name: String, type: XTypeName? = null, initializer: JavaCodeScope.Block) =
    variable(name, type) {
        final()
        initializer(initializer)
    }

fun JavaCodeBlockTrait.val_(
    name: String, type: KClass<*>, nullable: Boolean = false, initializer: JavaCodeScope.Block
) = val_(name, xType(type, nullable = nullable), initializer)

fun JavaCodeBlockTrait.val_(type: XTypeName? = null, initializer: JavaCodeScope.Block) =
    EagerDelegate { val_(it, type, initializer) }

fun JavaCodeBlockTrait.val_(type: KClass<*>, nullable: Boolean = false, initializer: JavaCodeScope.Block) =
    EagerDelegate { val_(it, type, nullable, initializer) }

inline fun <reified T> JavaCodeBlockTrait.val_(nullable: Boolean = true, noinline initializer: JavaCodeScope.Block) =
    EagerDelegate { val_(it, xType(T::class, nullable = nullable), initializer) }

inline fun <reified T : Any> JavaCodeBlockTrait.val_(noinline initializer: JavaCodeScope.Block) =
    val_<T>(nullable = false, initializer = initializer)

@JvmName("valUntypedInitializer")
fun JavaCodeBlockTrait.val_(initializer: JavaCodeScope.Block) =
    EagerDelegate { val_(it, type = null, initializer = initializer) }

fun JavaCodeBlockTrait.var_(name: String, type: XTypeName? = null, initializer: JavaCodeScope.Block) =
    variable(name, type) { initializer(initializer) }

fun JavaCodeBlockTrait.var_(
    name: String, type: KClass<*>, nullable: Boolean = false, initializer: JavaCodeScope.Block
) = var_(name, xType(type, nullable = nullable), initializer)

fun JavaCodeBlockTrait.var_(type: XTypeName? = null, initializer: JavaCodeScope.Block) =
    EagerDelegate { var_(it, type, initializer) }

fun JavaCodeBlockTrait.var_(type: KClass<*>, nullable: Boolean = false, initializer: JavaCodeScope.Block) =
    EagerDelegate { var_(it, type, nullable, initializer) }

inline fun <reified T> JavaCodeBlockTrait.var_(nullable: Boolean = true, noinline initializer: JavaCodeScope.Block) =
    EagerDelegate { var_(it, xType(T::class, nullable = nullable), initializer) }

inline fun <reified T : Any> JavaCodeBlockTrait.var_(noinline initializer: JavaCodeScope.Block) =
    var_<T>(nullable = false, initializer = initializer)

@JvmName("varUntypedInitializer")
fun JavaCodeBlockTrait.var_(initializer: JavaCodeScope.Block) =
    EagerDelegate { var_(it, type = null, initializer = initializer) }

internal class JavaCodeBlockContainer(
    val applyCodeBlockMutation: (type: XCodeBlockMutationType, codeBlock: JPCodeBlock) -> Unit
)

private val JavaCodeBlockTrait.container: JavaCodeBlockContainer
    get() = when (this) {
        is JavaBodyScope -> codeBlockContainer
        is JavaCodeBlockScope -> codeBlockContainer
        is JavaCodeBlockContainerScope -> codeBlockContainer
        is JavaControlFlowScope -> codeBlockContainer
    }
