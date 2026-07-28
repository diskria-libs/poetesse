package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava

@PoetesseJava
class JavaMultiLineCodeBlockScope : JavaStatementContainerScope.External {

    private val statements: MutableList<JPCodeBlock> = mutableListOf()

    internal val statementContainerInternalScope = JavaStatementContainerScope.Internal.of(
        append = { statements += it }
    )

    fun build(): JavaDeferredCodeBlock =
        JavaDeferredCodeBlock(statements)
}
