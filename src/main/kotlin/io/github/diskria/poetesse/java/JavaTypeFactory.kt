package io.github.diskria.poetesse.java

interface JavaTypeFactory : PoetesseJavaScope

fun JavaTypeFactory.type(kind: JPTypeKind, name: String, block: JavaTypeScope.Block = {}) =
    JavaTypeRef(name) { JavaTypeScope.of(kind, name, it).apply { block(it) }.build() }

fun JavaTypeFactory.class_(name: String, block: JavaTypeScope.Block = {}) =
    type(JPTypeKind.CLASS, name, block)

fun JavaTypeFactory.record_(name: String, block: JavaTypeScope.Block = {}) =
    type(JPTypeKind.RECORD, name, block)

fun JavaTypeFactory.interface_(name: String, block: JavaTypeScope.Block = {}) =
    type(JPTypeKind.INTERFACE, name, block)

fun JavaTypeFactory.enum_(name: String, block: JavaTypeScope.Block = {}) =
    type(JPTypeKind.ENUM, name, block)

fun JavaTypeFactory.annotation_(name: String, block: JavaTypeScope.Block = {}) =
    type(JPTypeKind.ANNOTATION, name, block)
