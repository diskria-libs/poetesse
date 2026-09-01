package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName
import kotlin.reflect.KClass

sealed interface KotlinAnnotationTrait : KotlinAnnotationFactory {
    operator fun KotlinAnnotationRef.unaryPlus() {
        container.append(spec)
    }
}

fun <A : Annotation> KotlinAnnotationTrait.annotation(
    className: XClassName, target: UseSite? = null, block: KotlinAnnotationScope.Block<A> = {}
) = +factory.annotation(className, target, block)

fun <A : Annotation> KotlinAnnotationTrait.annotation(
    type: KClass<out A>, target: UseSite? = null, block: KotlinAnnotationScope.Block<A> = {}
) = +factory.annotation(type, target, block)

inline fun <reified A : Annotation> KotlinAnnotationTrait.annotation(
    target: UseSite? = null, noinline block: KotlinAnnotationScope.Block<A> = {}
) = +factory.annotation<A>(target, block)

internal class KotlinAnnotationContainer(val append: (annotation: KPAnnotation) -> Unit)

@PublishedApi
internal val KotlinAnnotationTrait.factory: KotlinAnnotationFactory
    get() = this as KotlinAnnotationFactory

private val KotlinAnnotationTrait.container: KotlinAnnotationContainer
    get() = when (this) {
        is KotlinFileScope -> annotationContainer
        is AbstractKotlinBodyScope -> annotationContainer
        is KotlinTypeAliasScope -> annotationContainer
        is KotlinPropertyScope -> annotationContainer
        is KotlinPropertyGetterScope -> annotationContainer
        is KotlinPropertySetterScope -> annotationContainer
        is KotlinConstructorScope -> annotationContainer
        is KotlinFunctionScope -> annotationContainer
        is KotlinParameterScope -> annotationContainer
        is KotlinVariableScope -> annotationContainer
    }
