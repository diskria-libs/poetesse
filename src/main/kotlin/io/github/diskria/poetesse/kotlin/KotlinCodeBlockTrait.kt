package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.interop.XCodeBlockMutationType
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

sealed interface KotlinCodeBlockTrait : KotlinCodeBlockFactory {
    operator fun KotlinCodeBlockRef.unaryPlus() {
        +mutations
    }

    operator fun List<KotlinCodeBlockMutation>.unaryPlus() {
        forEach {
            if (it.type == XCodeBlockMutationType.ADD_STATEMENT) {
                +it.codeBlock
            } else {
                container.applyCodeBlockMutation(it.type, it.codeBlock)
            }
        }
    }

    operator fun KPCodeBlock.unaryPlus() {
        if (isEmpty()) return
        container.applyCodeBlockMutation(XCodeBlockMutationType.ADD_STATEMENT, this)
    }
}

fun KotlinCodeBlockTrait.line(block: KotlinCodeScope.Block) {
    +code(block).codeBlock
}

fun KotlinCodeBlockTrait.controlFlow(block: KotlinControlFlowScope.Block) {
    +KotlinControlFlowScope.of().apply(block).build()
}

fun KotlinCodeBlockTrait.variable(name: String, type: XTypeName?, block: KotlinVariableScope.Block = {}): String {
    line(KotlinVariableScope.of(name, type).apply(block).build())
    return name
}

@JvmName("variableUntyped")
fun KotlinCodeBlockTrait.variable(name: String, block: KotlinVariableScope.Block = {}) =
    variable(name, type = null, block)

fun KotlinCodeBlockTrait.variable(
    name: String,
    type: KClass<*>,
    nullable: Boolean = false,
    block: KotlinVariableScope.Block = {}
) = variable(name, xType(type, nullable = nullable), block)

fun KotlinCodeBlockTrait.variable(type: KClass<*>, nullable: Boolean = false, block: KotlinVariableScope.Block = {}) =
    EagerDelegate { variable(it, type, nullable, block) }

fun KotlinCodeBlockTrait.variable(type: XTypeName, block: KotlinVariableScope.Block = {}) =
    EagerDelegate { variable(it, type, block) }

@JvmName("variableUntypedDelegate")
fun KotlinCodeBlockTrait.variable(block: KotlinVariableScope.Block = {}) =
    EagerDelegate { variable(it, type = null, block) }

inline fun <reified T> KotlinCodeBlockTrait.variable(
    name: String,
    nullable: Boolean = true,
    noinline block: KotlinVariableScope.Block = {}
) = variable(name, T::class, nullable, block)

inline fun <reified T : Any> KotlinCodeBlockTrait.variable(
    name: String,
    noinline block: KotlinVariableScope.Block = {}
) = variable<T>(name, nullable = false, block)

inline fun <reified T> KotlinCodeBlockTrait.variable(
    nullable: Boolean = true,
    noinline block: KotlinVariableScope.Block = {}
) = EagerDelegate { variable<T>(it, nullable, block) }

inline fun <reified T : Any> KotlinCodeBlockTrait.variable(
    noinline block: KotlinVariableScope.Block = {}
) = variable<T>(nullable = false, block)

fun KotlinCodeBlockTrait.val_(name: String, type: XTypeName? = null, initializer: KotlinCodeScope.Block) =
    variable(name, type) { initializer(initializer) }

fun KotlinCodeBlockTrait.val_(
    name: String,
    type: KClass<*>,
    nullable: Boolean = false,
    initializer: KotlinCodeScope.Block
) = val_(name, xType(type, nullable = nullable), initializer)

fun KotlinCodeBlockTrait.val_(type: XTypeName? = null, initializer: KotlinCodeScope.Block) =
    EagerDelegate { val_(it, type, initializer) }

fun KotlinCodeBlockTrait.val_(type: KClass<*>, nullable: Boolean = false, initializer: KotlinCodeScope.Block) =
    EagerDelegate { val_(it, type, nullable, initializer) }

inline fun <reified T> KotlinCodeBlockTrait.val_(
    nullable: Boolean = true,
    noinline initializer: KotlinCodeScope.Block
) = EagerDelegate { val_(it, xType(T::class, nullable = nullable), initializer) }

inline fun <reified T : Any> KotlinCodeBlockTrait.val_(noinline initializer: KotlinCodeScope.Block) =
    val_<T>(nullable = false, initializer = initializer)

@JvmName("valUntypedInitializer")
fun KotlinCodeBlockTrait.val_(initializer: KotlinCodeScope.Block) =
    EagerDelegate { val_(it, type = null, initializer = initializer) }

fun KotlinCodeBlockTrait.var_(name: String, type: XTypeName? = null, initializer: KotlinCodeScope.Block) =
    variable(name, type) {
        mutable()
        initializer(initializer)
    }

fun KotlinCodeBlockTrait.var_(
    name: String,
    type: KClass<*>,
    nullable: Boolean = false,
    initializer: KotlinCodeScope.Block
) = var_(name, xType(type, nullable = nullable), initializer)

fun KotlinCodeBlockTrait.var_(type: XTypeName? = null, initializer: KotlinCodeScope.Block) =
    EagerDelegate { var_(it, type, initializer) }

fun KotlinCodeBlockTrait.var_(type: KClass<*>, nullable: Boolean = false, initializer: KotlinCodeScope.Block) =
    EagerDelegate { var_(it, type, nullable, initializer) }

inline fun <reified T> KotlinCodeBlockTrait.var_(
    nullable: Boolean = true,
    noinline initializer: KotlinCodeScope.Block
) = EagerDelegate { var_(it, xType(T::class, nullable = nullable), initializer) }

inline fun <reified T : Any> KotlinCodeBlockTrait.var_(noinline initializer: KotlinCodeScope.Block) =
    var_<T>(nullable = false, initializer = initializer)

@JvmName("varUntypedInitializer")
fun KotlinCodeBlockTrait.var_(initializer: KotlinCodeScope.Block) =
    EagerDelegate { var_(it, type = null, initializer = initializer) }

internal class KotlinCodeBlockContainer(
    val applyCodeBlockMutation: (type: XCodeBlockMutationType, codeBlock: KPCodeBlock) -> Unit
)

private val KotlinCodeBlockTrait.container: KotlinCodeBlockContainer
    get() = when (this) {
        is KotlinBodyScope -> codeBlockContainer
        is KotlinCodeBlockScope -> codeBlockContainer
        is KotlinCodeBlockContainerScope -> codeBlockContainer
        is KotlinControlFlowScope -> codeBlockContainer
    }
