package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.java.JPParameter
import io.github.diskria.poetesse.kotlin.KPParameter

class XParameter(val name: String = "", val type: XTypeName) {

    fun interopToKotlin(fallbackName: String): KPParameter =
        KPParameter(name.ifEmpty { fallbackName }, type.interopToKotlin())

    fun interopToJava(fallbackName: String): JPParameter =
        JPParameter.builder(type.interopToJava(), name.ifEmpty { fallbackName }).build()
}

context(scope: PoetesseXScope)
fun KPParameter.asXParameter(): XParameter =
    XParameter(name, scope.xType(type))

context(scope: PoetesseXScope)
fun JPParameter.asXParameter(): XParameter =
    XParameter(name(), scope.xType(type()))
