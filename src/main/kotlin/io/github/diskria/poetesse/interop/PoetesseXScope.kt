package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseX
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.kotlin.KPClassName
import io.github.diskria.poetesse.kotlin.KPTypeName
import kotlin.reflect.KClass

@PoetesseX
interface PoetesseXScope {
    val config: Poetesse.Config
}

fun PoetesseXScope.xType(kp: KPTypeName, nullable: Boolean = kp.isNullable, boxed: Boolean = nullable) =
    kp.setNullable(nullable).toXType(boxed)

fun PoetesseXScope.xType(type: KClass<*>, nullable: Boolean = false, boxed: Boolean = nullable) =
    type.toXType(nullable, boxed)

inline fun <reified T> PoetesseXScope.xType(nullable: Boolean = true, boxed: Boolean = nullable) =
    xType(T::class, nullable, boxed)

inline fun <reified T : Any> PoetesseXScope.xType(boxed: Boolean = false) = xType<T>(nullable = false, boxed = boxed)

fun PoetesseXScope.xType(jp: JPTypeName, nullable: Boolean = false) = jp.toXType(nullable)

fun PoetesseXScope.xClass(kp: KPClassName, nullable: Boolean = kp.isNullable) =
    kp.setNullable(nullable).asX<XClassName>()

fun PoetesseXScope.xClass(qualifiedName: String, nullable: Boolean = false) =
    xClass(KPClassName.bestGuess(qualifiedName), nullable)

fun PoetesseXScope.xClass(packageName: String?, simpleNames: Iterable<String>, nullable: Boolean = false) =
    XClassName.of(packageName, simpleNames.toList(), nullable)

fun PoetesseXScope.xClass(packageName: String?, vararg simpleNames: String, nullable: Boolean = false) =
    xClass(packageName, simpleNames.asIterable(), nullable)

fun PoetesseXScope.xClass(type: KClass<*>, nullable: Boolean = false) = type.toXClass(nullable)

inline fun <reified T> PoetesseXScope.xClass(nullable: Boolean = true) = xClass(T::class, nullable)
inline fun <reified T : Any> PoetesseXScope.xClass() = xClass<T>(nullable = false)
