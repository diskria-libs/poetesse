package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.xClass
import kotlin.reflect.KClass

interface KotlinAnnotationFactory : PoetesseKotlinScope

fun <A : Annotation> KotlinAnnotationFactory.annotation(
    className: XClassName, target: UseSite? = null, block: KotlinAnnotationScope.Block<A> = {}
) = KotlinTypedAnnotationRef<A> { KotlinAnnotationScope.of<A>(settings, className, target).apply(block).build() }

fun <A : Annotation> KotlinAnnotationFactory.annotation(
    type: KClass<out A>, target: UseSite? = null, block: KotlinAnnotationScope.Block<A> = {}
) = annotation(xClass(type), target, block)

inline fun <reified A : Annotation> KotlinAnnotationFactory.annotation(
    target: UseSite? = null, noinline block: KotlinAnnotationScope.Block<A> = {}
) = annotation(A::class, target, block)
