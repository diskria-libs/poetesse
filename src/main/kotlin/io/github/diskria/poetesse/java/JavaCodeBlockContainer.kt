package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate

sealed interface JavaCodeBlockContainer : JavaCodeBlockFactory {

    val expression: JavaCodeBlockExpressionScope
        get() = JavaCodeBlockExpressionScope(this)

    operator fun JavaCodeBlockRef.unaryPlus() {
        codeBlocks.forEach { +it }
    }

    fun line(build: JavaCodeBuilder) {
        +code(build).codeBlock
    }

    private operator fun JPCodeBlock.unaryPlus() {
        internal.append(this)
    }
}

@JvmInline
value class JavaCodeBlockExpressionScope internal constructor(val container: JavaCodeBlockContainer) {

    inline fun <reified T : Any> init(noinline value: JavaCodeBuilder): EagerDelegate<String> = EagerDelegate { name ->
        container.line { "${T<T>()} $name = ${L(value)}" }
        name
    }
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
        is JavaMethodScope.BodyScope -> codeBlockContainer
        is JavaCodeBlockScope -> codeBlockContainer
    }
