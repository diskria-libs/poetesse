package io.github.diskria.poetesse.java

interface JavaTypeFactory

fun JavaTypeFactory.type(kind: JPTypeKind, name: String, block: JavaTypeScope.() -> Unit = {}): JavaTypeRef =
    JavaTypeRef(name) { className -> JavaTypeScope.of(kind, name, className).apply(block).build() }

fun JavaTypeFactory.class_(name: String, block: JavaTypeScope.() -> Unit = {}): JavaTypeRef =
    type(JPTypeKind.CLASS, name, block)

fun JavaTypeFactory.record_(name: String, block: JavaTypeScope.() -> Unit = {}): JavaTypeRef =
    type(JPTypeKind.RECORD, name, block)

fun JavaTypeFactory.interface_(name: String, block: JavaTypeScope.() -> Unit = {}): JavaTypeRef =
    type(JPTypeKind.INTERFACE, name, block)

fun JavaTypeFactory.enum_(name: String, block: JavaTypeScope.() -> Unit = {}): JavaTypeRef =
    type(JPTypeKind.ENUM, name, block)

fun JavaTypeFactory.annotation_(name: String, block: JavaTypeScope.() -> Unit = {}): JavaTypeRef =
    type(JPTypeKind.ANNOTATION, name, block)
