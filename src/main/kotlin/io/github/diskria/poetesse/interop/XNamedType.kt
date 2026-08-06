package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.java.JPParameter
import io.github.diskria.poetesse.kotlin.KPParameter

class XNamedType(val name: String = "", val type: XTypeName) {

    fun interopToKotlinParameter(fallbackName: String = "p"): KPParameter =
        KPParameter(name.ifEmpty { fallbackName }, type.interopToKotlin())

    fun interopToKotlinFunctionalTypeParameter(): KPParameter =
        KPParameter(name, type.interopToKotlin())

    fun interopToJavaParameter(fallbackName: String = "p"): JPParameter =
        JPParameter.builder(type.interopToJava(), name.ifEmpty { fallbackName }).build()
}

fun KPParameter.asXNamedType(): XNamedType =
    XNamedType(name, type.asXTypeName())

fun JPParameter.asXNamedType(): XNamedType =
    XNamedType(name(), type().asXTypeName())
