package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.xClass
import kotlin.reflect.KClass

interface JavaAnnotationFactory : PoetesseJavaScope

fun <A : Annotation> JavaAnnotationFactory.annotation(
    className: XClassName,
    block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaTypedAnnotationRef<A> = JavaTypedAnnotationRef {
    JavaAnnotationScope.of<A>(settings, className).apply(block).build()
}

fun <A : Annotation> JavaAnnotationFactory.annotation(
    type: KClass<out A>,
    block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaTypedAnnotationRef<A> = annotation(xClass(type), block)

inline fun <reified A : Annotation> JavaAnnotationFactory.annotation(
    noinline block: JavaAnnotationScope<A>.() -> Unit = {}
): JavaTypedAnnotationRef<A> = annotation(A::class, block)
