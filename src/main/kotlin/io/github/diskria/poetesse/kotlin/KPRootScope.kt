package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin

@PoetesseKotlin
class KPRootScope(internal val settings: Poetesse.Settings) {
    typealias FileBuilder = com.squareup.kotlinpoet.FileSpec.Builder
    typealias File = com.squareup.kotlinpoet.FileSpec

    typealias TypeBuilder = com.squareup.kotlinpoet.TypeSpec.Builder
    typealias Type = com.squareup.kotlinpoet.TypeSpec

    typealias TypeAliasBuilder = com.squareup.kotlinpoet.TypeAliasSpec.Builder
    typealias TypeAlias = com.squareup.kotlinpoet.TypeAliasSpec

    typealias PropertyBuilder = com.squareup.kotlinpoet.PropertySpec.Builder
    typealias Property = com.squareup.kotlinpoet.PropertySpec

    typealias FunctionBuilder = com.squareup.kotlinpoet.FunSpec.Builder
    typealias Function = com.squareup.kotlinpoet.FunSpec

    typealias ParameterBuilder = com.squareup.kotlinpoet.ParameterSpec.Builder
    typealias Parameter = com.squareup.kotlinpoet.ParameterSpec

    typealias AnnotationBuilder = com.squareup.kotlinpoet.AnnotationSpec.Builder
    typealias Annotation = com.squareup.kotlinpoet.AnnotationSpec

    typealias CodeBlockBuilder = com.squareup.kotlinpoet.CodeBlock.Builder
    typealias CodeBlock = com.squareup.kotlinpoet.CodeBlock

    typealias TypeName = com.squareup.kotlinpoet.TypeName
    typealias ClassName = com.squareup.kotlinpoet.ClassName
    typealias ParameterizedTypeName = com.squareup.kotlinpoet.ParameterizedTypeName
    typealias TypeVariableName = com.squareup.kotlinpoet.TypeVariableName
    typealias WildcardTypeName = com.squareup.kotlinpoet.WildcardTypeName
    typealias LambdaTypeName = com.squareup.kotlinpoet.LambdaTypeName
    typealias Dynamic = com.squareup.kotlinpoet.Dynamic

    typealias Modifier = com.squareup.kotlinpoet.KModifier

    fun file(packageName: String?, fileName: String, block: KPFileScope.() -> Unit): KPFile =
        KPFileScope.of(packageName, fileName).apply(block).build(settings)
}
