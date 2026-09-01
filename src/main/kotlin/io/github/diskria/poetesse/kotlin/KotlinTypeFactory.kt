package io.github.diskria.poetesse.kotlin

interface KotlinTypeFactory : PoetesseKotlinScope

fun KotlinTypeFactory.type(kind: KPTypeKind, name: String, block: KotlinTypeScope.Block = {}) =
    KotlinTypeRef(name) { className -> KotlinTypeScope.of(kind, name, className).apply { block(className) }.build() }

fun KotlinTypeFactory.class_(name: String, block: KotlinTypeScope.Block = {}) =
    type(KPTypeKind.CLASS, name, block)

fun KotlinTypeFactory.value_class_(name: String, block: KotlinTypeScope.Block = {}) =
    class_(name) { className -> modifier(KPModifier.VALUE); block(className) }

fun KotlinTypeFactory.enum_class_(name: String, block: KotlinEnumTypeScope.Block = {}) =
    KotlinTypeRef(name) { className -> KotlinEnumTypeScope.of(name, className).apply { block(className) }.build() }

fun KotlinTypeFactory.data_class_(name: String, block: KotlinTypeScope.Block = {}) =
    class_(name) { className -> modifier(KPModifier.DATA); block(className) }

fun KotlinTypeFactory.annotation_class_(name: String, block: KotlinTypeScope.Block = {}) =
    class_(name) { className -> modifier(KPModifier.ANNOTATION); block(className) }

fun KotlinTypeFactory.expect_class_(name: String, block: KotlinTypeScope.Block = {}) =
    class_(name) { className -> expect(); block(className) }

fun KotlinTypeFactory.object_(name: String, block: KotlinTypeScope.Block = {}) =
    type(KPTypeKind.OBJECT, name, block)

fun KotlinTypeFactory.interface_(name: String, block: KotlinTypeScope.Block = {}) =
    type(KPTypeKind.INTERFACE, name, block)

fun KotlinTypeFactory.fun_interface_(name: String, block: KotlinTypeScope.Block = {}) =
    interface_(name) { className -> modifier(KPModifier.FUN); block(className) }
