package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.*
import io.github.diskria.poetesse.java.*
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

sealed class XTypeName {

    open val javaAsKotlin: KPTypeName
        get() = throw UnsupportedOperationException(
            "This type does not have a raw syntactic projection in Kotlin. Use interopToKotlin() instead."
        )

    open val kotlinAsJava: JPTypeName
        get() = throw UnsupportedOperationException(
            "This type does not have a raw syntactic projection in Java. Use interopToJava() instead."
        )

    abstract val nullable: Boolean

    abstract fun interopToKotlin(): KPTypeName
    abstract fun interopToJava(): JPTypeName
    internal abstract fun setNullableInternal(nullable: Boolean): XTypeName

    fun toKotlin(interop: Boolean): KPTypeName = if (interop) interopToKotlin() else javaAsKotlin
    fun toJava(interop: Boolean): JPTypeName = if (interop) interopToJava() else kotlinAsJava

    companion object {
        inline fun <reified T : Any> of(): XTypeName =
            T::class.asXTypeName()
    }
}

fun KPTypeName.asXTypeName(): XTypeName = when (this) {
    is ClassName -> {
        asXVoidTypeNameOrNull() ?: asXPrimitiveTypeNameOrNull() ?: asXArrayTypeNameOrNull() ?: asXClassName()
    }

    is ParameterizedTypeName -> asXArrayTypeNameOrNull() ?: asXParameterizedTypeName()
    is TypeVariableName -> TODO("XTypeVariableName is not implemented")
    is WildcardTypeName -> TODO("XWildcardTypeName is not implemented")
    is LambdaTypeName -> TODO("XFunctionTypeName is not implemented")
    Dynamic -> error("XTypeName is only supported on Kotlin/JVM (Kotlin/JS is unsupported)")
}

fun JPTypeName.asXTypeName(): XTypeName = when (this) {
    is JPClassName -> asXClassName()
    is JPArrayTypeName -> asXArrayTypeName()
    is JPParameterizedTypeName -> asXParameterizedTypeName()
    is JPTypeVariableName -> TODO("XTypeVariableName is not implemented")
    is JPWildcardTypeName -> TODO("XWildcardTypeName is not implemented")
    else -> asXVoidTypeNameOrNull() ?: asXPrimitiveTypeName()
}

fun KClass<out Any>.asXTypeName(): XTypeName =
    asXVoidTypeNameOrNull() ?: asXPrimitiveTypeNameOrNull() ?: asXClassName()

@Suppress("UNCHECKED_CAST")
fun <T : XTypeName> T.setNullable(nullable: Boolean): T =
    if (nullable == this.nullable) this
    else setNullableInternal(nullable) as T
