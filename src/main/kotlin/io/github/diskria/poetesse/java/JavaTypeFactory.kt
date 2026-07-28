package io.github.diskria.poetesse.java

interface JavaTypeFactory

fun JavaTypeFactory.type(kind: JPTypeKind, name: String, block: JavaTypeScope.() -> Unit = {}): JavaDeferredType =
    JavaDeferredType(name) { className -> JavaTypeScope.of(kind, name, className).apply(block).build() }

fun JavaTypeFactory.class_(name: String, block: JavaTypeScope.() -> Unit = {}): JavaDeferredType =
    type(JPTypeKind.CLASS, name, block)

fun JavaTypeFactory.record_(name: String, block: JavaTypeScope.() -> Unit = {}): JavaDeferredType =
    type(JPTypeKind.RECORD, name, block)

fun JavaTypeFactory.interface_(name: String, block: JavaTypeScope.() -> Unit = {}): JavaDeferredType =
    type(JPTypeKind.INTERFACE, name, block)

fun JavaTypeFactory.enum_(name: String, block: JavaTypeScope.() -> Unit = {}): JavaDeferredType =
    type(JPTypeKind.ENUM, name, block)

fun JavaTypeFactory.annotation_(name: String, block: JavaTypeScope.() -> Unit = {}): JavaDeferredType =
    type(JPTypeKind.ANNOTATION, name, block)
