package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XClassName
import kotlin.reflect.KClass

sealed interface JavaAnnotationContainer : JavaAnnotationFactory {

    operator fun JavaAnnotationRef.unaryPlus() {
        internal.append(spec)
    }
}

fun <A : Annotation> JavaAnnotationContainer.annotation(
    className: XClassName,
    block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    +factory.annotation(className, block)
}

fun <A : Annotation> JavaAnnotationContainer.annotation(
    kClass: KClass<out A>,
    block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    annotation(XClassName.of(kClass), block)
}

inline fun <reified A : Annotation> JavaAnnotationContainer.annotation(
    noinline block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    annotation(A::class, block)
}

internal interface JavaAnnotationContainerInternal {

    fun append(annotation: JPAnnotation)

    companion object {
        fun of(
            append: (annotation: JPAnnotation) -> Unit,
        ): JavaAnnotationContainerInternal = object : JavaAnnotationContainerInternal {
            override fun append(annotation: JPAnnotation) = append(annotation)
        }
    }
}

private val JavaAnnotationContainer.factory: JavaAnnotationFactory
    get() = this as JavaAnnotationFactory

private val JavaAnnotationContainer.internal: JavaAnnotationContainerInternal
    get() = when (this) {
        is JavaTypeScope -> annotationContainer
        is JavaFieldScope -> annotationContainer
        is JavaMethodScope -> annotationContainer
        is JavaVariableScope -> annotationContainer
    }
