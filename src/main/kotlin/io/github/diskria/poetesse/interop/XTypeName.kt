package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.*
import io.github.diskria.poetesse.java.*
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

sealed class XTypeName {

    abstract val isNullable: Boolean

    abstract fun interopToKotlin(): KPTypeName
    abstract fun interopToJava(): JPTypeName
    internal abstract fun setNullable(nullable: Boolean): XTypeName
}

@Suppress("UNCHECKED_CAST")
fun <T : XTypeName> T.nullable(nullable: Boolean = true): T =
    if (nullable == isNullable) this
    else setNullable(nullable) as T

internal fun XTypeName.ensureBoxed(): XTypeName =
    if (this is XPrimitiveTypeName) box() else this

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

fun KClass<*>.xType(nullable: Boolean = false): XTypeName {
    require(java.typeParameters.isEmpty()) {
        "xClass for generics is not possible.\n" +
            "For example, for List<String> use:\n" +
            "xClass<List<*>>().generic(xType<String>())"
    }
    return asXVoidTypeNameOrNull(nullable) ?: asXPrimitiveTypeNameOrNull(nullable) ?: xClass(nullable)
}

inline fun <reified T> xType(nullable: Boolean = true) =
    T::class.xType(nullable)

inline fun <reified T : Any> xType(): XTypeName =
    xType<T>(nullable = false)

fun KPTypeName.interopToJavaPoet(): JPTypeName =
    asXTypeName().interopToJava()

fun JPTypeName.interopToKotlinPoet(): KPTypeName =
    asXTypeName().interopToKotlin()
