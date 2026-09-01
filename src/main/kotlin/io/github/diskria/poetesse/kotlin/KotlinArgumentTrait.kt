package io.github.diskria.poetesse.kotlin

sealed interface KotlinArgumentTrait : PoetesseKotlinScope {

    fun argument(codeBlock: KPCodeBlock) {
        container.append(codeBlock)
    }

    fun argument(name: String, nameAsComment: Boolean, value: KotlinCodeRef) {
        val prefix = name.ifEmpty { null }?.let { if (nameAsComment) "/* $it = */ " else "$it = " }.orEmpty()
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

internal class KotlinArgumentContainer(val append: (codeBlock: KPCodeBlock) -> Unit)

private val KotlinArgumentTrait.container: KotlinArgumentContainer
    get() = when (this) {
        is KotlinSuperclassConstructorScope -> argumentContainer
    }
