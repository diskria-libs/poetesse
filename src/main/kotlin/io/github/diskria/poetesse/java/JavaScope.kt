package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.PoetesseSettings

@PoetesseJava
class JavaScope(internal val settings: PoetesseSettings) {
    typealias FileBuilder = com.palantir.javapoet.JavaFile.Builder
    typealias File = com.palantir.javapoet.JavaFile

    typealias TypeBuilder = com.palantir.javapoet.TypeSpec.Builder
    typealias Type = com.palantir.javapoet.TypeSpec

    typealias FieldBuilder = com.palantir.javapoet.FieldSpec.Builder
    typealias Field = com.palantir.javapoet.FieldSpec

    typealias MethodBuilder = com.palantir.javapoet.MethodSpec.Builder
    typealias Method = com.palantir.javapoet.MethodSpec

    typealias ParameterBuilder = com.palantir.javapoet.ParameterSpec.Builder
    typealias Parameter = com.palantir.javapoet.ParameterSpec

    typealias AnnotationBuilder = com.palantir.javapoet.AnnotationSpec.Builder
    typealias Annotation = com.palantir.javapoet.AnnotationSpec

    typealias CodeBlockBuilder = com.palantir.javapoet.CodeBlock.Builder
    typealias CodeBlock = com.palantir.javapoet.CodeBlock

    typealias TypeName = com.palantir.javapoet.TypeName
    typealias ClassName = com.palantir.javapoet.ClassName
    typealias ArrayTypeName = com.palantir.javapoet.ArrayTypeName
    typealias ParameterizedTypeName = com.palantir.javapoet.ParameterizedTypeName
    typealias TypeVariableName = com.palantir.javapoet.TypeVariableName
    typealias WildcardTypeName = com.palantir.javapoet.WildcardTypeName

    typealias Modifier = javax.lang.model.element.Modifier

    fun file(packageName: String?, name: String, block: JPFileScope.() -> Unit): JPFile =
        JPFileScope.of(packageName, name).apply(block).build(settings)
}
