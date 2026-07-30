package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate

class JavaCodeBlockContainerScope private constructor() {

    sealed interface External : JavaCodeBlockFactory, JavaCodeFactory {

        fun line(buildCode: JavaCodeBuilder) {
            internal.append(code(buildCode).codeBlock)
        }

        operator fun JavaDeferredCodeBlock.unaryPlus() {
            if (codeBlocks.isEmpty()) return
            codeBlocks.forEach { internal.append(it) }
        }
    }

    internal interface Internal {

        fun append(codeBlock: JPCodeBlock)

        companion object {
            fun of(
                append: (codeBlock: JPCodeBlock) -> Unit,
            ): Internal = object : Internal {
                override fun append(codeBlock: JPCodeBlock) = append(codeBlock)
            }
        }
    }
}

private val JavaCodeBlockContainerScope.External.internal: JavaCodeBlockContainerScope.Internal
    get() = when (this) {
        is JavaMethodScope.Body -> codeBlockContainerInternalScope
        is JavaCodeBlockScope -> codeBlockContainerInternalScope
    }

inline fun <reified T> JavaCodeBlockContainerScope.External.initAssign(
    noinline buildValueCode: JavaCodeBuilder
): EagerDelegate<String> = EagerDelegate { name ->
    line { "${T<T>()} $name = ${L(code(buildValueCode))}" }
    name
}
