package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass

interface JavaAnnotationFactory

fun <A : Annotation> JavaAnnotationFactory.annotation(
    className: XClassName,
    block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaDeferredAnnotation<A> = JavaDeferredAnnotation {
    JavaAnnotationScope.of<A>(className).apply(block).specBuilder.build()
}

fun <A : Annotation> JavaAnnotationFactory.annotation(
    kClass: KClass<out A>,
    block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaDeferredAnnotation<A> =
    annotation(XClassName.of(kClass), block)

inline fun <reified A : Annotation> JavaAnnotationFactory.annotation(
    noinline block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaDeferredAnnotation<A> =
    annotation(A::class, block)
