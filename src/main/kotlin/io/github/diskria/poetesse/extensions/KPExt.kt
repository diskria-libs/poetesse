package io.github.diskria.poetesse.extensions

import io.github.diskria.poetesse.kotlin.*

val KPClassName.qualifiedName: String
    get() = canonicalName

@Suppress("UNCHECKED_CAST")
fun <T : KPTypeName> T.setNullable(nullable: Boolean): T =
    if (nullable == isNullable) this
    else copy(nullable = nullable) as T

@Suppress("UNCHECKED_CAST")
fun <T : KPTypeName> T.withoutAnnotations(): T =
    if (annotations.isEmpty()) this
    else copy(annotations = emptyList()) as T

fun KPFunctionBuilder.addStatement(codeBlock: KPCodeBlock) {
    addCode("«")
    addCode(codeBlock)
    addCode("\n»")
}

fun KPCodeBlockBuilder.addStatement(codeBlock: KPCodeBlock) {
    add("«")
    add(codeBlock)
    add("\n»")
}
