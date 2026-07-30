package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass

interface JavaAnnotationFactory

fun <A : Annotation> JavaAnnotationFactory.annotation(
    kClass: KClass<out A>,
    block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaDeferredAnnotation<A> =
    JavaAnnotationScope.of(kClass).apply(block).build()

inline fun <reified A : Annotation> JavaAnnotationFactory.annotation(
    noinline block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaDeferredAnnotation<A> =
    JavaAnnotationScope.of<A>().apply(block).build()

fun JavaAnnotationFactory.annotation(
    className: XClassName,
    block: JavaAnnotationScope<Annotation>.() -> Unit = {}
): JavaDeferredAnnotation<Annotation> =
    JavaAnnotationScope.of(className).apply(block).build()
