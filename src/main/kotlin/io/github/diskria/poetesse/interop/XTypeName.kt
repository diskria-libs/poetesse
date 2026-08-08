package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.*
import io.github.diskria.poetesse.java.*
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

sealed class XTypeName {

    abstract val nullable: Boolean

    abstract fun interopToKotlin(): KPTypeName
    abstract fun interopToJava(): JPTypeName
    internal abstract fun setNullableInternal(nullable: Boolean): XTypeName
}

@Suppress("UNCHECKED_CAST")
fun <T : XTypeName> T.setNullable(nullable: Boolean): T =
    if (nullable == this.nullable) this
    else setNullableInternal(nullable) as T

fun KPTypeName.asXTypeName(): XTypeName = when (this) {
    is ClassName -> {
        asXVoidTypeNameOrNull() ?: asXPrimitiveTypeNameOrNull() ?: asXArrayTypeNameOrNull() ?: asXClassName()
    }

    is ParameterizedTypeName -> asXArrayTypeNameOrNull() ?: asXParameterizedTypeName()
    is TypeVariableName -> asXTypeVariableName()
    is WildcardTypeName -> asXWildcardTypeName()
    is LambdaTypeName -> asXFunctionalTypeName()
    Dynamic -> error("XTypeName is only supported on Kotlin/JVM (Kotlin/JS is unsupported)")
}

fun JPTypeName.asXTypeName(): XTypeName = when (this) {
    is JPClassName -> asXVoidTypeNameOrNull() ?: asXPrimitiveTypeNameOrNull() ?: asXClassName()
    is JPArrayTypeName -> asXArrayTypeName()
    is JPParameterizedTypeName -> asXFunctionalTypeNameOrNull() ?: asXParameterizedTypeName()
    is JPTypeVariableName -> asXTypeVariableName()
    is JPWildcardTypeName -> asXWildcardTypeName()
    is JPTypeName -> asXVoidTypeNameOrNull() ?: asXPrimitiveTypeName()
}

fun KClass<out Any>.xType(nullable: Boolean = false): XTypeName =
    asXVoidTypeNameOrNull(nullable) ?: asXPrimitiveTypeNameOrNull(nullable) ?: asXClassName(nullable)

inline fun <reified T : Any> xType(): XTypeName =
    T::class.xType()

fun KPTypeName.interopToJavaPoet(): JPTypeName =
    asXTypeName().interopToJava()

fun JPTypeName.interopToKotlinPoet(): KPTypeName =
    asXTypeName().interopToKotlin()
