package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.xClass
import kotlin.reflect.KClass

interface KotlinAnnotationFactory : PoetesseScope

fun <A : Annotation> KotlinAnnotationFactory.annotation(
    className: XClassName,
    block: KotlinAnnotationScope<A>.() -> Unit = {}
): KotlinTypedAnnotationRef<A> = KotlinTypedAnnotationRef {
    KotlinAnnotationScope.of<A>(settings, className).apply(block).build()
}

fun <A : Annotation> KotlinAnnotationFactory.annotation(
    type: KClass<out A>,
    block: KotlinAnnotationScope<A>.() -> Unit = {}
): KotlinTypedAnnotationRef<A> =
    annotation(xClass(type), block)

inline fun <reified A : Annotation> KotlinAnnotationFactory.annotation(
    noinline block: KotlinAnnotationScope<A>.() -> Unit = {}
): KotlinTypedAnnotationRef<A> =
    annotation(A::class, block)
