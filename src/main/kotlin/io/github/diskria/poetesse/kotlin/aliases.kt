package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.*

typealias KPFileBuilder = FileSpec.Builder
typealias KPFile = FileSpec

typealias KPTypeBuilder = TypeSpec.Builder
typealias KPType = TypeSpec
typealias KPTypeKind = TypeSpec.Kind

typealias KPTypeAliasBuilder = TypeAliasSpec.Builder
typealias KPTypeAlias = TypeAliasSpec

typealias KPPropertyBuilder = PropertySpec.Builder
typealias KPProperty = PropertySpec

typealias KPFunctionBuilder = FunSpec.Builder
typealias KPFunction = FunSpec

typealias KPParameterBuilder = ParameterSpec.Builder
typealias KPParameter = ParameterSpec

typealias KPAnnotationBuilder = AnnotationSpec.Builder
typealias KPAnnotation = AnnotationSpec

typealias KPModifier = KModifier

typealias KPCodeBlockBuilder = CodeBlock.Builder
typealias KPCodeBlock = CodeBlock

typealias KPTypeName = TypeName
typealias KPClassName = ClassName
typealias KPParameterizedTypeName = ParameterizedTypeName
typealias KPTypeVariableName = TypeVariableName
typealias KPWildcardTypeName = WildcardTypeName
typealias KPLambdaTypeName = LambdaTypeName
typealias KPDynamic = Dynamic
