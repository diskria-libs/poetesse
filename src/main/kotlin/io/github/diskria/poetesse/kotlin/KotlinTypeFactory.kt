package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XTypeName

interface KotlinTypeFactory : PoetesseKotlinScope

fun KotlinTypeFactory.typeAlias(name: String, type: XTypeName, block: KotlinTypeAliasScope.Block = {}) =
    KotlinTypeAliasRef(name) { KotlinTypeAliasScope.of(settings, name, type).apply(block).build() }

fun KotlinTypeFactory.type(kind: KPTypeKind, name: String, block: KotlinTypeScope.Block = {}) =
    KotlinTypeRef(name) { KotlinTypeScope.of(settings, kind, name, it).apply { block(it) }.build() }

fun KotlinTypeFactory.class_(name: String, block: KotlinTypeScope.Block = {}) =
    type(KPTypeKind.CLASS, name, block)

fun KotlinTypeFactory.value_class_(name: String, block: KotlinTypeScope.Block = {}) =
    class_(name) { modifiers(KPModifier.VALUE); block(it) }

fun KotlinTypeFactory.enum_class_(name: String, block: KotlinTypeScope.Block = {}) =
    class_(name) { modifiers(KPModifier.ENUM); block(it) }

fun KotlinTypeFactory.data_class_(name: String, block: KotlinTypeScope.Block = {}) =
    class_(name) { modifiers(KPModifier.DATA); block(it) }

fun KotlinTypeFactory.annotation_class_(name: String, block: KotlinTypeScope.Block = {}) =
    class_(name) { modifiers(KPModifier.ANNOTATION); block(it) }

fun KotlinTypeFactory.object_(name: String, block: KotlinTypeScope.Block = {}) =
    type(KPTypeKind.OBJECT, name, block)

fun KotlinTypeFactory.interface_(name: String, block: KotlinTypeScope.Block = {}) =
    type(KPTypeKind.INTERFACE, name, block)

fun KotlinTypeFactory.fun_interface_(name: String, block: KotlinTypeScope.Block = {}) =
    interface_(name) { modifiers(KPModifier.FUN); block(it) }
