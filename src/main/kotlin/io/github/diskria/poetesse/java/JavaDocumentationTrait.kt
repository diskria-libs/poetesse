package io.github.diskria.poetesse.java

sealed interface JavaDocumentationTrait : PoetesseJavaScope

fun JavaDocumentationTrait.documentation(block: JavaCodeBlockScope.Block) {
    container.append(JavaCodeBlockScope.of(isComment = true).apply(block).build())
}

internal class JavaDocumentationContainer(val append: (codeBlock: JPCodeBlock) -> Unit)

private val JavaDocumentationTrait.container: JavaDocumentationContainer
    get() = when (this) {
        is AbstractJavaBodyScope -> documentationContainer
        is JavaFieldScope -> documentationContainer
        is JavaConstructorScope -> documentationContainer
        is JavaMethodScope -> documentationContainer
        is JavaParameterScope -> documentationContainer
    }
