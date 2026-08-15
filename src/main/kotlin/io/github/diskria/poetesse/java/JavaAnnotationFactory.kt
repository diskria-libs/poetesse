package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.xClass
import kotlin.reflect.KClass

interface JavaAnnotationFactory : PoetesseJavaScope

fun <A : Annotation> JavaAnnotationFactory.annotation(
    className: XClassName, block: JavaAnnotationScope.Block<A> = {}
) = JavaTypedAnnotationRef<A> { JavaAnnotationScope.of<A>(className).apply(block).build() }

fun <A : Annotation> JavaAnnotationFactory.annotation(type: KClass<out A>, block: JavaAnnotationScope.Block<A> = {}) =
    annotation(xClass(type), block)

inline fun <reified A : Annotation> JavaAnnotationFactory.annotation(
    noinline block: JavaAnnotationScope.Block<A> = {}
) = annotation(A::class, block)
