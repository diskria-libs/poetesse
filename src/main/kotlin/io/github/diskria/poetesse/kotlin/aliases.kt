package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.*

typealias KPFileBuilder = FileSpec.Builder
typealias KPFile = FileSpec

typealias KPTypeHolderBuilder = TypeSpecHolder.Builder<*>
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
typealias KPFunctionalTypeName = LambdaTypeName

val KPUnit: KPClassName = UNIT

val KPBoolean: KPClassName = BOOLEAN
val KPByte: KPClassName = BYTE
val KPShort: KPClassName = SHORT
val KPInt: KPClassName = INT
val KPLong: KPClassName = LONG
val KPChar: KPClassName = CHAR
val KPFloat: KPClassName = FLOAT
val KPDouble: KPClassName = DOUBLE

val KPArray: KPClassName = ARRAY

val KPAny: KPClassName = ANY

val KPStar: KPWildcardTypeName = STAR
