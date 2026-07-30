package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass

sealed interface JavaAnnotationContainer : JavaAnnotationFactory {

    operator fun JavaDeferredAnnotation.unaryPlus() {
        internal.append(spec)
    }
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

fun <A : Annotation> JavaAnnotationContainer.annotation(
    className: XClassName,
    block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    +(this as JavaAnnotationFactory).annotation(className, block)
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

private val JavaAnnotationContainer.internal: JavaAnnotationContainerInternal
    get() = when (this) {
        is JavaTypeScope -> annotationContainer
        is JavaMethodScope -> annotationContainer
    }
