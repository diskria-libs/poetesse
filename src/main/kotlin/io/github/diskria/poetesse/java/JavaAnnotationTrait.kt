package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XClassName
import kotlin.reflect.KClass

sealed interface JavaAnnotationTrait : JavaAnnotationFactory {
    operator fun JavaAnnotationRef.unaryPlus() = container.append(spec)
}

fun <A : Annotation> JavaAnnotationTrait.annotation(
    className: XClassName, block: JavaAnnotationScope.Block<A> = {}
) = +factory.annotation(className, block)

fun <A : Annotation> JavaAnnotationTrait.annotation(type: KClass<out A>, block: JavaAnnotationScope.Block<A> = {}) =
    +factory.annotation(type, block)

inline fun <reified A : Annotation> JavaAnnotationTrait.annotation(
    noinline block: JavaAnnotationScope.Block<A> = {}
) = +factory.annotation<A>(block)

internal class JavaAnnotationContainer(val append: (annotation: JPAnnotation) -> Unit)

@PublishedApi
internal val JavaAnnotationTrait.factory: JavaAnnotationFactory
    get() = this as JavaAnnotationFactory

private val JavaAnnotationTrait.container: JavaAnnotationContainer
    get() = when (this) {
        is AbstractJavaBodyScope -> annotationContainer
        is JavaFieldScope -> annotationContainer
        is JavaConstructorScope -> annotationContainer
        is JavaMethodScope -> annotationContainer
        is JavaParameterScope -> annotationContainer
        is JavaVariableScope -> annotationContainer
    }
