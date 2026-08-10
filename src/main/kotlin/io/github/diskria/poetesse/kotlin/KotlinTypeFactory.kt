package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseScope

interface KotlinTypeFactory : PoetesseScope

fun KotlinTypeFactory.type(kind: KPTypeKind, name: String, block: KotlinTypeScope.() -> Unit = {}): KotlinTypeRef =
    KotlinTypeRef(name) { className -> KotlinTypeScope.of(settings, kind, name, className).apply(block).build() }

fun KotlinTypeFactory.class_(name: String, block: KotlinTypeScope.() -> Unit = {}): KotlinTypeRef =
    type(KPTypeKind.CLASS, name, block)

fun KotlinTypeFactory.value_class_(name: String, block: KotlinTypeScope.() -> Unit = {}): KotlinTypeRef =
    class_(name) {
        modifiers(KPModifier.VALUE)
        block()
    }

fun KotlinTypeFactory.enum_class_(name: String, block: KotlinTypeScope.() -> Unit = {}): KotlinTypeRef =
    class_(name) {
        modifiers(KPModifier.ENUM)
        block()
    }

fun KotlinTypeFactory.data_class_(name: String, block: KotlinTypeScope.() -> Unit = {}): KotlinTypeRef =
    class_(name) {
        modifiers(KPModifier.DATA)
        block()
    }

fun KotlinTypeFactory.annotation_class_(name: String, block: KotlinTypeScope.() -> Unit = {}): KotlinTypeRef =
    class_(name) {
        modifiers(KPModifier.ANNOTATION)
        block()
    }

fun KotlinTypeFactory.object_(name: String, block: KotlinTypeScope.() -> Unit = {}): KotlinTypeRef =
    type(KPTypeKind.OBJECT, name, block)

fun KotlinTypeFactory.interface_(name: String, block: KotlinTypeScope.() -> Unit = {}): KotlinTypeRef =
    type(KPTypeKind.INTERFACE, name, block)

fun KotlinTypeFactory.fun_interface_(name: String, block: KotlinTypeScope.() -> Unit = {}): KotlinTypeRef =
    interface_(name) {
        modifiers(KPModifier.FUN)
        block()
    }
