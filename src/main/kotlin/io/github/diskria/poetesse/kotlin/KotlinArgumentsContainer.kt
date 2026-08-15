package io.github.diskria.poetesse.kotlin

sealed interface KotlinArgumentsContainer : PoetesseKotlinScope {

    fun argument(codeBlock: KPCodeBlock) {
        internal.append(codeBlock)
    }

    fun argument(name: String, nameAsComment: Boolean, value: KotlinCodeRef) {
        val prefix = if (name.isNotEmpty()) {
            if (nameAsComment) "/* $name = */ " else "$name = "
        } else {
            ""
        }
        argument(KotlinCodeScope.of { prefix + L(value) }.codeBlock)
    }

    fun argument(name: String, value: KotlinCodeRef) {
        argument(name, false, value)
    }

    fun argument(name: String, nameAsComment: Boolean, block: KotlinCodeScope.Block) {
        argument(name, nameAsComment, KotlinCodeScope.of(block))
    }

    fun argument(name: String, block: KotlinCodeScope.Block) {
        argument(name, false, block)
    }

    fun argument(value: KotlinCodeRef) {
        argument("", false, value)
    }

    fun argument(block: KotlinCodeScope.Block) {
        argument("", false, block)
    }
}

internal class KotlinArgumentsContainerInternal(val append: (codeBlock: KPCodeBlock) -> Unit)

private val KotlinArgumentsContainer.internal: KotlinArgumentsContainerInternal
    get() = when (this) {
        is KotlinTypeScope.SuperclassConstructorScope -> argumentsContainer
    }
