package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass

class JavaAnnotationConfigScope private constructor() {

    sealed interface External : JavaAnnotationFactory {
        operator fun JavaDeferredAnnotation.unaryPlus() {
            internal.append(this)
        }
    }

    internal interface Internal {

        fun append(annotation: JavaDeferredAnnotation)

        companion object {
            fun of(
                append: (annotation: JavaDeferredAnnotation) -> Unit,
            ): Internal = object : Internal {
                override fun append(annotation: JavaDeferredAnnotation) = append(annotation)
            }
        }
    }
}

fun <A : Annotation> JavaAnnotationConfigScope.External.annotation(
    className: XClassName,
    block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    internal.append((this as JavaAnnotationFactory).annotation(className, block))
}

fun <A : Annotation> JavaAnnotationConfigScope.External.annotation(
    kClass: KClass<out A>,
    block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    annotation(XClassName.of(kClass), block)
}

inline fun <reified A : Annotation> JavaAnnotationConfigScope.External.annotation(
    noinline block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    annotation(A::class, block)
}

private val JavaAnnotationConfigScope.External.internal: JavaAnnotationConfigScope.Internal
    get() = when (this) {
        is JavaTypeScope -> annotationConfigInternalScope
        is JavaMethodScope -> annotationConfigInternalScope
    }
