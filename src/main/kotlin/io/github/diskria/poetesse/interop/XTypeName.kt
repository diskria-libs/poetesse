package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

sealed interface XTypeName {

    val javaAsKotlin: KPTypeName
        get() = throw UnsupportedOperationException(
            "This type does not have a raw syntactic projection in Kotlin. Use interopToKotlin() instead."
        )

    val kotlinAsJava: JPTypeName
        get() = throw UnsupportedOperationException(
            "This type does not have a raw syntactic projection in Java. Use interopToJava() instead."
        )

    val isNullable: Boolean

    fun interopToKotlin(): KPTypeName
    fun interopToJava(): JPTypeName

    fun toKotlin(interop: Boolean): KPTypeName = if (interop) interopToKotlin() else javaAsKotlin
    fun toJava(interop: Boolean): JPTypeName = if (interop) interopToJava() else kotlinAsJava

    companion object {
        fun of(kClass: KClass<out Any>, isNullable: Boolean = false): XTypeName =
            XPrimitiveTypeName.ofOrNull(kClass, isNullable) ?: XClassName.of(kClass, isNullable)

        inline fun <reified T : Any> of(isNullable: Boolean = false): XTypeName =
            of(T::class, isNullable)
    }
}
