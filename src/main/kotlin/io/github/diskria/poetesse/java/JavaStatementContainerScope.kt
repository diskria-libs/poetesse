package io.github.diskria.poetesse.java

class JavaStatementContainerScope private constructor() {

    sealed interface External {

        fun line(statement: JavaStatementBuilder) {
            internal.append(JavaStatementScope.create(statement))
        }
    }

    internal interface Internal {

        fun append(statement: JPCodeBlock)

        companion object {
            internal fun of(
                append: (statement: JPCodeBlock) -> Unit,
            ): Internal = object : Internal {
                override fun append(statement: JPCodeBlock) = append(statement)
            }
        }
    }
}

@PublishedApi
internal val JavaStatementContainerScope.External.internal: JavaStatementContainerScope.Internal
    get() = when (this) {
        is JavaMethodScope.Body -> statementContainerInternalScope
    }
