package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava

@PoetesseJava
class JavaMethodScope private constructor(
    private val specBuilder: JPMethodBuilder
) : JavaModifierConfigScope.External {

    internal val modifierConfigInternalScope = JavaModifierConfigScope.Internal.of(
        append = { specBuilder.addModifiers(*it) }
    )

    fun body(block: Body.() -> Unit) {
        Body().apply(block)
    }

    fun static() {
        modifiers(JPModifier.STATIC)
    }

    internal fun build(): JPMethod =
        specBuilder.build()

    inner class Body : JavaStatementContainerScope.External {
        internal val statementContainerInternalScope = JavaStatementContainerScope.Internal.of(
            append = { specBuilder.addStatement(it) }
        )
    }

    internal companion object {
        fun of(name: String): JavaMethodScope =
            JavaMethodScope(JPMethod.methodBuilder(name))
    }
}
