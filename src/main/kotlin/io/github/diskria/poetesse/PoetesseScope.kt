package io.github.diskria.poetesse

import io.github.diskria.poetesse.extensions.capitalized
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.interop.*
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.kotlin.KPClassName
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

@PoetesseX
interface PoetesseScope {
    val settings: Poetesse.Settings
}

fun PoetesseScope.xType(kp: KPTypeName, nullable: Boolean = kp.isNullable, boxed: Boolean = nullable): XTypeName =
    kp.setNullable(nullable).toXType(boxed)

fun PoetesseScope.xType(type: KClass<*>, nullable: Boolean = false, boxed: Boolean = nullable): XTypeName =
    type.toXType(nullable, boxed)

inline fun <reified T> PoetesseScope.xType(nullable: Boolean = true, boxed: Boolean = nullable) =
    xType(T::class, nullable, boxed)

inline fun <reified T : Any> PoetesseScope.xType(boxed: Boolean = false): XTypeName =
    xType<T>(nullable = false, boxed = boxed)

fun PoetesseScope.xType(jp: JPTypeName, nullable: Boolean = false): XTypeName =
    jp.toXType(nullable)

fun PoetesseScope.xClass(kp: KPClassName, nullable: Boolean = kp.isNullable): XClassName =
    kp.setNullable(nullable).asX<XClassName>()

fun PoetesseScope.xClass(qualifiedName: String, nullable: Boolean = false): XClassName =
    xClass(KPClassName.bestGuess(qualifiedName), nullable)

fun PoetesseScope.xClass(packageName: String?, simpleNames: Iterable<String>, nullable: Boolean = false): XClassName =
    XClassName(settings, packageName, simpleNames.toList(), nullable)

fun PoetesseScope.xClass(packageName: String?, vararg simpleNames: String, nullable: Boolean = false): XClassName =
    xClass(packageName, simpleNames.asIterable(), nullable)

fun PoetesseScope.xClass(type: KClass<*>, nullable: Boolean = false): XClassName =
    type.toXClass(nullable)

inline fun <reified T> PoetesseScope.xClass(nullable: Boolean = true): XClassName =
    xClass(T::class, nullable)

inline fun <reified T : Any> PoetesseScope.xClass(): XClassName =
    xClass<T>(nullable = false)

fun PoetesseScope.typeVariable(
    name: String,
    bounds: Iterable<XTypeName> = emptyList(),
    variance: XVariance? = null,
    reified: Boolean = false,
    nullable: Boolean = false,
): XTypeVariableName = XTypeVariableName(settings, name, bounds.toList(), variance, reified, nullable)

fun PoetesseScope.typeVariable(
    name: String,
    vararg bounds: XTypeName,
    variance: XVariance? = null,
    reified: Boolean = false,
    nullable: Boolean = false,
) = typeVariable(name, bounds.asIterable(), variance, reified, nullable)

fun PoetesseScope.typeVariable(
    bounds: Iterable<XTypeName> = emptyList(),
    variance: XVariance? = null,
    reified: Boolean = false,
    nullable: Boolean = false,
) = EagerDelegate { name -> typeVariable(name.capitalized(), bounds, variance, reified, nullable) }

fun PoetesseScope.typeVariable(
    vararg bounds: XTypeName,
    variance: XVariance? = null,
    reified: Boolean = false,
    nullable: Boolean = false,
) = EagerDelegate { name -> typeVariable(name.capitalized(), bounds.asIterable(), variance, reified, nullable) }
