package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.extensions.isBoxedVoid
import io.github.diskria.poetesse.extensions.setBoxed
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.java.JPBoxedVoid
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.java.JPVoid
import io.github.diskria.poetesse.kotlin.KPTypeName
import io.github.diskria.poetesse.kotlin.KPUnit
import kotlin.reflect.KClass

class XVoidTypeName private constructor(override val nullable: Boolean = false) : XTypeName() {

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
    if (setNullable(false).withoutAnnotations() == KPUnit) XVoidTypeName.get(isNullable)
    else null

fun KPTypeName.asXVoidTypeName(): XVoidTypeName =
    requireNotNull(asXVoidTypeNameOrNull()) { "$this is not a void type" }

fun JPTypeName.asXVoidTypeNameOrNull(): XVoidTypeName? =
    if (setBoxed(false).withoutAnnotations() == JPVoid) XVoidTypeName.get(isBoxedVoid)
    else null

fun JPTypeName.asXVoidTypeName(): XVoidTypeName =
    requireNotNull(asXVoidTypeNameOrNull()) { "$this is not a void type" }

fun KClass<out Any>.asXVoidTypeNameOrNull(nullable: Boolean = false): XVoidTypeName? =
    asClassName().setNullable(nullable).asXVoidTypeNameOrNull()

fun KClass<out Any>.asXVoidTypeName(nullable: Boolean = false): XVoidTypeName =
    asClassName().setNullable(nullable).asXVoidTypeName()
