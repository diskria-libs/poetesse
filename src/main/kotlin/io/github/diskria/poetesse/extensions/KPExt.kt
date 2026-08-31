package io.github.diskria.poetesse.extensions

import io.github.diskria.poetesse.interop.XCodeBlockMutationType
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

fun KPFunctionBuilder.beginControlFlow(codeBlock: KPCodeBlock): KPFunctionBuilder =
    beginControlFlow("%L", codeBlock)

fun KPFunctionBuilder.nextControlFlow(codeBlock: KPCodeBlock): KPFunctionBuilder =
    nextControlFlow("%L", codeBlock)

fun KPFunctionBuilder.endControlFlow(codeBlock: KPCodeBlock): KPFunctionBuilder =
    addCode("⇤}%L\n", codeBlock)

fun KPFunctionBuilder.addStatement(codeBlock: KPCodeBlock) {
    addCode("«")
    addCode(codeBlock)
    addCode("\n»")
}

fun KPCodeBlockBuilder.beginControlFlow(codeBlock: KPCodeBlock): KPCodeBlockBuilder =
    beginControlFlow("%L", codeBlock)

fun KPCodeBlockBuilder.nextControlFlow(codeBlock: KPCodeBlock): KPCodeBlockBuilder =
    nextControlFlow("%L", codeBlock)

fun KPCodeBlockBuilder.endControlFlow(controlFlow: String, vararg args: Any?): KPCodeBlockBuilder {
    unindent()
    add("} $controlFlow\n", *args)
    return this
}

fun KPCodeBlockBuilder.endControlFlow(codeBlock: KPCodeBlock): KPCodeBlockBuilder =
    endControlFlow("%L", codeBlock)

fun KPCodeBlockBuilder.addStatement(codeBlock: KPCodeBlock) {
    add("«")
    add(codeBlock)
    add("\n»")
}

internal fun KPFunctionBuilder.applyCodeBlockMutation(type: XCodeBlockMutationType, codeBlock: KPCodeBlock) {
    when (type) {
        XCodeBlockMutationType.ADD_STATEMENT -> addStatement(codeBlock)
        XCodeBlockMutationType.BEGIN_CONTROL_FLOW -> beginControlFlow(codeBlock)
        XCodeBlockMutationType.NEXT_CONTROL_FLOW -> nextControlFlow(codeBlock)
        XCodeBlockMutationType.END_CONTROL_FLOW -> endControlFlow(codeBlock)
    }
}

internal fun KPCodeBlockBuilder.applyCodeBlockMutation(type: XCodeBlockMutationType, codeBlock: KPCodeBlock) {
    when (type) {
        XCodeBlockMutationType.ADD_STATEMENT -> addStatement(codeBlock)
        XCodeBlockMutationType.BEGIN_CONTROL_FLOW -> beginControlFlow(codeBlock)
        XCodeBlockMutationType.NEXT_CONTROL_FLOW -> nextControlFlow(codeBlock)
        XCodeBlockMutationType.END_CONTROL_FLOW -> endControlFlow(codeBlock)
    }
}
