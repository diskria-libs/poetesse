package io.github.diskria.poetesse.kotlin

sealed interface KotlinDocumentationTrait : PoetesseKotlinScope

fun KotlinDocumentationTrait.documentation(block: KotlinCodeBlockScope.Block) {
    container.append(KotlinCodeBlockScope.of().apply(block).build())
}

internal class KotlinDocumentationContainer(val append: (codeBlock: KPCodeBlock) -> Unit)

private val KotlinDocumentationTrait.container: KotlinDocumentationContainer
    get() = when (this) {
        is AbstractKotlinBodyScope -> documentationContainer
        is KotlinTypeAliasScope -> documentationContainer
        is KotlinPropertyScope -> documentationContainer
        is KotlinPropertyGetterScope -> documentationContainer
        is KotlinPropertySetterScope -> documentationContainer
        is KotlinConstructorScope -> documentationContainer
        is KotlinFunctionScope -> documentationContainer
        is KotlinParameterScope -> documentationContainer
    }
