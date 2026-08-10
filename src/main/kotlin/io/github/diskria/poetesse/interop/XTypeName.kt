package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.java.*
import io.github.diskria.poetesse.kotlin.*
import io.github.diskria.poetesse.xClass
import kotlin.reflect.KClass

sealed class XTypeName<K : KPTypeName, J : JPTypeName> : PoetesseScope {

    internal open val isBoxed: Boolean = true
    internal abstract val isNullable: Boolean

    internal open fun boxInternal(): XTypeName<K, J> = this

    internal abstract fun interopToKotlinInternal(): K
    internal abstract fun interopToJavaInternal(): J
}

fun <K : KPTypeName, X : XTypeName<K, *>> X.interopToKotlin(): K =
    interopToKotlinInternal().setNullable(isNullable)

fun <J : JPTypeName, X : XTypeName<*, J>> X.box(): XTypeName<*, J> =
    if (isBoxed) this
    else boxInternal()

@Suppress("UNCHECKED_CAST")
fun <J : JPTypeName, X : XTypeName<*, J>> X.interopToJava(resolveNullability: Boolean = true): J {
    val jp = interopToJavaInternal()
    if (resolveNullability && isBoxed) {
        return settings.javaNullabilityResolver.setNullable(jp, isNullable)
    }
    return jp
}

@PublishedApi
context(scope: PoetesseScope)
internal inline fun <reified X : XTypeName<*, *>> KPTypeName.asXOrNull(boxed: Boolean = isNullable): X? =
    when (X::class) {
        XVoidTypeName::class -> asXVoidTypeNameOrNull(boxed)
        XPrimitiveTypeName::class -> asXPrimitiveTypeNameOrNull(boxed)
        XClassName::class if (this is KPClassName) -> asXClassName()
        XArrayTypeName::class if (this is KPParameterizedTypeName) -> asXArrayTypeNameOrNull()
        XFunctionalTypeName::class if (this is KPFunctionalTypeName) -> asXFunctionalTypeName()
        XParameterizedTypeName::class if (this is KPParameterizedTypeName) -> asXParameterizedTypeName()
        XTypeVariableName::class if (this is KPTypeVariableName) -> asXTypeVariableName()
        XWildcardTypeName::class if (this is KPWildcardTypeName) -> asXWildcardTypeName()
        else -> null
    } as X?

context(scope: PoetesseScope)
internal inline fun <reified X : XTypeName<*, *>> KPTypeName.asX(boxed: Boolean = isNullable): X =
    requireNotNull(asXOrNull<X>(boxed)) {
        "Cannot convert KPTypeName '${this::class.simpleName}' ($this) to X-interop '${X::class.simpleName}'"
    }

context(scope: PoetesseScope)
internal fun KPTypeName.toXType(boxed: Boolean = isNullable): XTypeName<*, *> = when (this) {
    is KPClassName -> {
        asXOrNull<XVoidTypeName>(boxed)
            ?: asXOrNull<XPrimitiveTypeName>(boxed)
            ?: asXOrNull<XArrayTypeName>()
            ?: asX<XClassName>()
    }

    is KPParameterizedTypeName -> asXOrNull<XArrayTypeName>() ?: asX<XParameterizedTypeName>()
    is KPTypeVariableName -> asX<XTypeVariableName>()
    is KPWildcardTypeName -> asX<XWildcardTypeName>()
    is KPFunctionalTypeName -> asX<XFunctionalTypeName>()
    Dynamic -> error("XTypeName is only supported on Kotlin/JVM (Kotlin/JS is unsupported)")
}

@PublishedApi
context(scope: PoetesseScope)
internal fun KClass<*>.toXType(nullable: Boolean = false, boxed: Boolean = nullable): XTypeName<*, *> {
    require(java.typeParameters.isEmpty()) {
        val className = simpleName ?: this.toString()
        buildString {
            appendLine("Cannot create XTypeName directly from parameterized class '$className'.")
            appendLine()
            appendLine("To construct generic types, use:")
            appendLine("  For List<String>: xClass<List<*>>().generic(xType<String>())")
            appendLine()
            appendLine("To construct lambda types, use:")
            appendLine("  For (Int) -> String: xType<String>().lambda(xType<Int>())")
        }
    }
    val kp = asClassName().setNullable(nullable)
    return kp.asXOrNull<XVoidTypeName>(boxed) ?: kp.asXOrNull<XPrimitiveTypeName>(boxed) ?: scope.xClass(this, nullable)
}

@PublishedApi
context(scope: PoetesseScope)
internal inline fun <reified X : XTypeName<*, *>> JPTypeName.asXOrNull(nullable: Boolean = false): X? =
    when (X::class) {
        XVoidTypeName::class -> asXVoidTypeNameOrNull(nullable)
        XPrimitiveTypeName::class -> asXPrimitiveTypeNameOrNull(nullable)
        XClassName::class if (this is JPClassName) -> asXClassName(nullable)
        XArrayTypeName::class if (this is JPArrayTypeName) -> asXArrayTypeName(nullable)
        XFunctionalTypeName::class if (this is JPParameterizedTypeName) -> asXFunctionalTypeNameOrNull(nullable)
        XParameterizedTypeName::class if (this is JPParameterizedTypeName) -> asXParameterizedTypeName(nullable)
        XTypeVariableName::class if (this is JPTypeVariableName) -> asXTypeVariableName(nullable)
        XWildcardTypeName::class if (this is JPWildcardTypeName) -> asXWildcardTypeName(nullable)
        else -> null
    } as X?

context(scope: PoetesseScope)
internal inline fun <reified X : XTypeName<*, *>> JPTypeName.asX(nullable: Boolean = false): X =
    requireNotNull(asXOrNull<X>(nullable)) {
        "Cannot convert JPTypeName '${this::class.simpleName}' ($this) to X-interop '${X::class.simpleName}'"
    }

context(scope: PoetesseScope)
internal fun JPTypeName.toXType(nullable: Boolean = false): XTypeName<*, *> = when (this) {
    is JPClassName -> {
        asXOrNull<XVoidTypeName>(nullable)
            ?: asXOrNull<XPrimitiveTypeName>(nullable)
            ?: asX<XClassName>(nullable)
    }

    is JPArrayTypeName -> asX<XArrayTypeName>(nullable)
    is JPParameterizedTypeName -> asXOrNull<XFunctionalTypeName>(nullable) ?: asX<XParameterizedTypeName>(nullable)
    is JPTypeVariableName -> asX<XTypeVariableName>(nullable)
    is JPWildcardTypeName -> asX<XWildcardTypeName>(nullable)
    is JPTypeName -> asXOrNull<XVoidTypeName>(nullable) ?: asX<XPrimitiveTypeName>(nullable)
}
