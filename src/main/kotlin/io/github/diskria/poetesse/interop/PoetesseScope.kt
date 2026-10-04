package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseX
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.interop.XWildcardTypeName.Companion.of
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.kotlin.KPClassName
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

@PoetesseX
interface PoetesseScope {
    val config: Poetesse.Config
}

fun PoetesseScope.xType(kp: KPTypeName, nullable: Boolean = kp.isNullable, boxed: Boolean = nullable): XTypeName =
    kp.setNullable(nullable).toXType(boxed)

fun PoetesseScope.xType(
    packageName: String?,
    simpleNames: Iterable<String>,
    typeArguments: List<XTypeName> = emptyList(),
    nullable: Boolean = false,
    boxed: Boolean = nullable,
): XTypeName {
    val xType = KPClassName(packageName.orEmpty(), simpleNames.toList()).setNullable(nullable).toXType(boxed)
    if (xType !is XClassName || typeArguments.isEmpty()) return xType
    return XArrayTypeName.fromGenericOrNull(xType, typeArguments, nullable) ?: xType.generic(typeArguments)
}

fun PoetesseScope.xType(type: KClass<*>, nullable: Boolean = false, boxed: Boolean = nullable): XTypeName =
    type.toXType(nullable, boxed)

inline fun <reified T> PoetesseScope.xType(nullable: Boolean = true, boxed: Boolean = nullable): XTypeName =
    xType(T::class, nullable, boxed)

inline fun <reified T : Any> PoetesseScope.xType(boxed: Boolean = false): XTypeName =
    xType<T>(nullable = false, boxed = boxed)

fun PoetesseScope.xType(
    jp: JPTypeName,
    nullable: Boolean = config.javaNullabilityResolver.isNullable(jp),
): XTypeName = jp.toXType(nullable)

fun PoetesseScope.xClass(kp: KPClassName, nullable: Boolean = kp.isNullable): XClassName =
    kp.setNullable(nullable).asX<XClassName>()

fun PoetesseScope.xClass(qualifiedName: String, nullable: Boolean = false): XClassName =
    xClass(KPClassName.bestGuess(qualifiedName), nullable)

fun PoetesseScope.xClass(packageName: String?, simpleNames: Iterable<String>, nullable: Boolean = false): XClassName =
    XClassName.of(packageName, simpleNames.toList(), nullable)

fun PoetesseScope.xClass(packageName: String?, vararg simpleNames: String, nullable: Boolean = false): XClassName =
    xClass(packageName, simpleNames.asIterable(), nullable)

fun PoetesseScope.xClass(type: KClass<*>, nullable: Boolean = false): XClassName =
    type.toXClass(nullable)

inline fun <reified T> PoetesseScope.xClass(nullable: Boolean = true): XClassName =
    xClass(T::class, nullable)

inline fun <reified T : Any> PoetesseScope.xClass(): XClassName =
    xClass<T>(nullable = false)

fun PoetesseScope.xClass(
    jp: JPClassName,
    nullable: Boolean = config.javaNullabilityResolver.isNullable(jp),
): XClassName = jp.asX<XClassName>(nullable)

fun PoetesseScope.xWildcard(inT: XTypeName?, outT: XTypeName?): XWildcardTypeName =
    of(inType = inT, outType = outT)

fun PoetesseScope.xStar(): XWildcardTypeName = xWildcard(null, null)
