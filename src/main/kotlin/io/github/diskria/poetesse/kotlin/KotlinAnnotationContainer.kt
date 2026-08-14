package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName
import kotlin.reflect.KClass

sealed interface KotlinAnnotationContainer : KotlinAnnotationFactory {

    operator fun KotlinAnnotationRef.unaryPlus() {
        internal.append(spec)
    }
}

fun <A : Annotation> KotlinAnnotationContainer.annotation(
    className: XClassName,
    target: UseSite? = null,
    block: KotlinAnnotationScope<A>.() -> Unit = {}
) = +factory.annotation(className, target, block)

fun <A : Annotation> KotlinAnnotationContainer.annotation(
    type: KClass<out A>,
    target: UseSite? = null,
    block: KotlinAnnotationScope<A>.() -> Unit = {}
) = +factory.annotation(type, target, block)

inline fun <reified A : Annotation> KotlinAnnotationContainer.annotation(
    target: UseSite? = null,
    noinline block: KotlinAnnotationScope<A>.() -> Unit = {}
) = +factory.annotation<A>(target, block)

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

@PublishedApi
internal val KotlinAnnotationContainer.factory: KotlinAnnotationFactory
    get() = this as KotlinAnnotationFactory

private val KotlinAnnotationContainer.internal: KotlinAnnotationContainerInternal
    get() = when (this) {
        is KotlinTypeScope -> annotationContainer
        is KotlinTypeAliasScope -> annotationContainer
        is KotlinPropertyScope -> annotationContainer
        is KotlinPropertyGetterScope -> annotationContainer
        is KotlinPropertySetterScope -> annotationContainer
        is KotlinConstructorScope -> annotationContainer
        is KotlinFunctionScope -> annotationContainer
        is KotlinParameterScope -> annotationContainer
        is KotlinVariableScope -> annotationContainer
    }
