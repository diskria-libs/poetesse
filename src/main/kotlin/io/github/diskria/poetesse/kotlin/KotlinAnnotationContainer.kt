package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.xClass
import kotlin.reflect.KClass

sealed interface KotlinAnnotationContainer : KotlinAnnotationFactory {

    operator fun KotlinAnnotationRef.unaryPlus() {
        internal.append(spec)
    }
}

fun <A : Annotation> KotlinAnnotationContainer.annotation(
    className: XClassName,
    block: KotlinAnnotationScope<A>.() -> Unit = {}
) {
    +factory.annotation(className, block)
}

fun <A : Annotation> KotlinAnnotationContainer.annotation(
    type: KClass<out A>,
    block: KotlinAnnotationScope<A>.() -> Unit = {}
) {
    annotation(type.xClass(), block)
}

inline fun <reified A : Annotation> KotlinAnnotationContainer.annotation(
    noinline block: KotlinAnnotationScope<A>.() -> Unit = {}
) {
    annotation(A::class, block)
}

internal interface KotlinAnnotationContainerInternal {

    fun append(annotation: KPAnnotation)

    companion object {
        fun of(
            append: (annotation: KPAnnotation) -> Unit,
        ): KotlinAnnotationContainerInternal = object : KotlinAnnotationContainerInternal {
            override fun append(annotation: KPAnnotation) = append(annotation)
        }
    }
}

private val KotlinAnnotationContainer.factory: KotlinAnnotationFactory
    get() = this as KotlinAnnotationFactory

private val KotlinAnnotationContainer.internal: KotlinAnnotationContainerInternal
    get() = when (this) {
        is KotlinTypeScope -> annotationContainer
//        is KotlinFieldScope -> annotationContainer
        is KotlinConstructorScope -> annotationContainer
        is KotlinFunctionScope -> annotationContainer
        is KotlinParameterScope -> annotationContainer
        is KotlinVariableScope -> annotationContainer
    }
