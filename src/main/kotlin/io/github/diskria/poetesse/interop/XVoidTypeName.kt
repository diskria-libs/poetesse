package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.extensions.isVoid
import io.github.diskria.poetesse.extensions.setBoxed
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

class XVoidTypeName(override val nullable: Boolean = false) : XTypeName() {

    override val kotlinAsJava: JPClassName =
        com.squareup.kotlinpoet.UNIT.asXClassName().kotlinAsJava

    override fun interopToKotlin(): KPTypeName =
        com.squareup.kotlinpoet.UNIT.setNullable(nullable)

    override fun interopToJava(): JPTypeName =
        JPTypeName.VOID.setBoxed(nullable)

    override fun setNullableInternal(nullable: Boolean): XVoidTypeName =
        XVoidTypeName(nullable)
}

fun KPTypeName.asXVoidTypeNameOrNull(): XVoidTypeName? =
    if (withoutAnnotations().setNullable(false) == com.squareup.kotlinpoet.UNIT) XVoidTypeName(isNullable)
    else null

fun JPTypeName.asXVoidTypeNameOrNull(): XVoidTypeName? =
    if (isVoid) XVoidTypeName()
    else null

fun KClass<out Any>.asXVoidTypeNameOrNull(): XVoidTypeName? =
    asClassName().asXVoidTypeNameOrNull()
