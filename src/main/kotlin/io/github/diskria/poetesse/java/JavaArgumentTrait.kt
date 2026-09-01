package io.github.diskria.poetesse.java

sealed interface JavaArgumentTrait : PoetesseJavaScope {

    fun argument(codeBlock: JPCodeBlock) {
        container.append(codeBlock)
    }

    fun argument(name: String? = null, value: JavaCodeRef) {
        val prefix = name?.ifEmpty { null }?.let { "/* $it = */ " }.orEmpty()
        argument(JavaCodeScope.of { prefix + L(value) }.codeBlock)
    }

    fun argument(name: String? = null, block: JavaCodeScope.Block) {
        argument(name, JavaCodeScope.of(block))
    }
}

internal class JavaArgumentContainer(val append: (codeBlock: JPCodeBlock) -> Unit)

private val JavaArgumentTrait.container: JavaArgumentContainer
    get() = when (this) {
        is JavaSuperclassConstructorScope -> argumentContainer
    }
