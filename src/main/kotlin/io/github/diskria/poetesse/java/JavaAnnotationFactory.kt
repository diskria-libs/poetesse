package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.xClass
import kotlin.reflect.KClass

interface JavaAnnotationFactory

fun <A : Annotation> JavaAnnotationFactory.annotation(
    className: XClassName,
    block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaTypedAnnotationRef<A> = JavaTypedAnnotationRef {
    JavaAnnotationScope.of<A>(className).apply(block).build()
}

fun <A : Annotation> JavaAnnotationFactory.annotation(
    type: KClass<out A>,
    block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaTypedAnnotationRef<A> =
    annotation(type.xClass(), block)

inline fun <reified A : Annotation> JavaAnnotationFactory.annotation(
    noinline block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaTypedAnnotationRef<A> =
    annotation(A::class, block)
