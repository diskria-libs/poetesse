package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate

sealed interface JavaCodeBlockContainer : JavaCodeBlockFactory, JavaCodeFactory {

    operator fun JavaCodeRef.unaryPlus() {
        +codeBlock
    }

    operator fun JavaCodeBlockRef.unaryPlus() {
        codeBlocks.forEach { +it }
    }

    fun line(buildCode: JavaCodeBuilder) {
        +code(buildCode)
    }

    private operator fun JPCodeBlock.unaryPlus() {
        internal.append(this)
    }
}

inline fun <reified T> JavaCodeBlockContainer.initAssign(
    noinline buildValueCode: JavaCodeBuilder
): EagerDelegate<String> = EagerDelegate { name ->
    line { "${T<T>()} $name = ${L(code(buildValueCode))}" }
    name
}

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
        is JavaMethodScope.Body -> codeBlockContainer
        is JavaCodeBlockScope -> codeBlockContainer
    }
