package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass

class JavaAnnotationConfigScope private constructor() {

    sealed interface External : JavaAnnotationFactory {
        operator fun JavaDeferredAnnotation<*>.unaryPlus() {
            internal.append(this)
        }
    }

    @PublishedApi internal interface Internal {

        fun append(annotation: JavaDeferredAnnotation<*>)

        companion object {
            internal fun of(
                append: (annotation: JavaDeferredAnnotation<*>) -> Unit,
            ): Internal = object : Internal {
                override fun append(annotation: JavaDeferredAnnotation<*>) = append(annotation)
            }
        }
    }
}

fun <A : Annotation> JavaAnnotationConfigScope.External.annotation(
    kClass: KClass<out A>,
    block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    internal.append((this as JavaAnnotationFactory).annotation(kClass, block))
}

inline fun <reified A : Annotation> JavaAnnotationConfigScope.External.annotation(
    noinline block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    internal.append((this as JavaAnnotationFactory).annotation<A>(block))
}

fun JavaAnnotationConfigScope.External.annotation(
    className: XClassName,
    block: JavaAnnotationScope<Annotation>.() -> Unit = {}
) {
    internal.append((this as JavaAnnotationFactory).annotation(className, block))
}

@PublishedApi
internal val JavaAnnotationConfigScope.External.internal: JavaAnnotationConfigScope.Internal
    get() = when (this) {
        is JavaTypeScope -> annotationConfigInternalScope
    }
