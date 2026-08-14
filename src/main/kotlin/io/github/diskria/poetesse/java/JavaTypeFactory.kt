package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.interop.XClassName

interface JavaTypeFactory : PoetesseScope

fun JavaTypeFactory.type(
    kind: JPTypeKind, name: String, block: JavaTypeScope.(className: XClassName) -> Unit = {}
): JavaTypeRef =
    JavaTypeRef(name) { className ->
        JavaTypeScope.of(settings, kind, name, className).apply { block(className) }.build()
    }

fun JavaTypeFactory.class_(name: String, block: JavaTypeScope.(className: XClassName) -> Unit = {}): JavaTypeRef =
    type(JPTypeKind.CLASS, name, block)

fun JavaTypeFactory.record_(name: String, block: JavaTypeScope.(className: XClassName) -> Unit = {}): JavaTypeRef =
    type(JPTypeKind.RECORD, name, block)

fun JavaTypeFactory.interface_(name: String, block: JavaTypeScope.(className: XClassName) -> Unit = {}): JavaTypeRef =
    type(JPTypeKind.INTERFACE, name, block)

fun JavaTypeFactory.enum_(name: String, block: JavaTypeScope.(className: XClassName) -> Unit = {}): JavaTypeRef =
    type(JPTypeKind.ENUM, name, block)

fun JavaTypeFactory.annotation_(name: String, block: JavaTypeScope.(className: XClassName) -> Unit = {}): JavaTypeRef =
    type(JPTypeKind.ANNOTATION, name, block)
