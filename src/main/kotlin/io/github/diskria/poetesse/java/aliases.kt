package io.github.diskria.poetesse.java

import com.palantir.javapoet.*

typealias JPFileBuilder = JavaFile.Builder
typealias JPFile = JavaFile

typealias JPTypeBuilder = TypeSpec.Builder
typealias JPType = TypeSpec
typealias JPTypeKind = TypeSpec.Kind

typealias JPFieldBuilder = FieldSpec.Builder
typealias JPField = FieldSpec

typealias JPMethodBuilder = MethodSpec.Builder
typealias JPMethod = MethodSpec

typealias JPParameterBuilder = ParameterSpec.Builder
typealias JPParameter = ParameterSpec

typealias JPAnnotation = AnnotationSpec
typealias JPAnnotationBuilder = AnnotationSpec.Builder

typealias JPModifier = javax.lang.model.element.Modifier

typealias JPCodeBlockBuilder = CodeBlock.Builder
typealias JPCodeBlock = CodeBlock

typealias JPTypeName = TypeName
typealias JPClassName = ClassName
typealias JPArrayTypeName = ArrayTypeName
typealias JPParameterizedTypeName = ParameterizedTypeName
typealias JPTypeVariableName = TypeVariableName
typealias JPWildcardTypeName = WildcardTypeName
