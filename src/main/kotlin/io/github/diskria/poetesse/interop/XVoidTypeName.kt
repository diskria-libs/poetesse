package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.extensions.isVoid
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.java.JPBoxedVoid
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.java.JPVoid
import io.github.diskria.poetesse.kotlin.KPTypeName
import io.github.diskria.poetesse.kotlin.KPUnit
import kotlin.reflect.KClass

class XVoidTypeName private constructor(override val nullable: Boolean = false) : XTypeName() {

    override val javaAsKotlin: KPTypeName
        get() = if (nullable) JPBoxedVoid.asXClassName().javaAsKotlin else super.javaAsKotlin

    override val kotlinAsJava: JPClassName = KPUnit.asXClassName().kotlinAsJava

    override fun interopToKotlin(): KPTypeName = KPUnit.setNullable(nullable)

    override fun interopToJava(): JPTypeName = if (nullable) JPBoxedVoid else JPVoid

    override fun setNullableInternal(nullable: Boolean): XVoidTypeName = get(nullable)

    internal companion object {
        private val NON_BOXED: XVoidTypeName = XVoidTypeName(false)
        private val BOXED: XVoidTypeName = XVoidTypeName(true)

        fun get(nullable: Boolean = false): XVoidTypeName =
            if (nullable) BOXED else NON_BOXED
    }
}

fun KPTypeName.asXVoidTypeNameOrNull(): XVoidTypeName? =
    if (withoutAnnotations().setNullable(false) == KPUnit) XVoidTypeName.get(isNullable)
    else null

fun JPTypeName.asXVoidTypeNameOrNull(): XVoidTypeName? =
    if (isVoid) XVoidTypeName.get()
    else null

fun KClass<out Any>.asXVoidTypeNameOrNull(): XVoidTypeName? =
    asClassName().asXVoidTypeNameOrNull()
