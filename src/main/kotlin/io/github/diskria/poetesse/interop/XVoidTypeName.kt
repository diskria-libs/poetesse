package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.extensions.isVoid
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.kotlin.KPTypeName

class XVoidTypeName(override val nullable: Boolean = false) : XTypeName() {

    override fun interopToKotlin(): KPTypeName =
        com.squareup.kotlinpoet.UNIT.setNullable(nullable)

    override fun interopToJava(): JPTypeName =
        JPTypeName.VOID

    override fun setNullableInternal(nullable: Boolean): XVoidTypeName =
        XVoidTypeName(nullable)
}

fun KPTypeName.asXVoidTypeNameOrNull(): XVoidTypeName? =
    if (withoutAnnotations().setNullable(false) == com.squareup.kotlinpoet.UNIT) XVoidTypeName(isNullable)
    else null

fun JPTypeName.asXVoidTypeNameOrNull(): XVoidTypeName? =
    if (isVoid) XVoidTypeName()
    else null
