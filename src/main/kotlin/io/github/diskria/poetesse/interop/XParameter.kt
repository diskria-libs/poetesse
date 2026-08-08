package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.java.JPParameter
import io.github.diskria.poetesse.kotlin.KPParameter

class XParameter(val name: String = "", val type: XTypeName) {

    fun interopToKotlin(fallbackName: String): KPParameter =
        KPParameter(name.ifEmpty { fallbackName }, type.interopToKotlin())

    fun interopToJava(fallbackName: String): JPParameter =
        JPParameter.builder(type.interopToJava(), name.ifEmpty { fallbackName }).build()
}

fun KPParameter.asXParameter(): XParameter =
    XParameter(name, type.asXTypeName())

fun JPParameter.asXParameter(): XParameter =
    XParameter(name(), type().asXTypeName())
