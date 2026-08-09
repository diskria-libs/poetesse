package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.xClass
import kotlin.reflect.KClass

interface KotlinAnnotationFactory

fun <A : Annotation> KotlinAnnotationFactory.annotation(
    className: XClassName,
    block: KotlinAnnotationScope<A>.() -> Unit = {}
): KotlinTypedAnnotationRef<A> = KotlinTypedAnnotationRef {
    KotlinAnnotationScope.of<A>(className).apply(block).build()
}

fun <A : Annotation> KotlinAnnotationFactory.annotation(
    type: KClass<out A>,
    block: KotlinAnnotationScope<A>.() -> Unit = {}
): KotlinTypedAnnotationRef<A> =
    annotation(type.xClass(), block)

inline fun <reified A : Annotation> KotlinAnnotationFactory.annotation(
    noinline block: KotlinAnnotationScope<A>.() -> Unit = {}
): KotlinTypedAnnotationRef<A> =
    annotation(A::class, block)
