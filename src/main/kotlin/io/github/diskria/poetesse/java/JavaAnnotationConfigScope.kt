package io.github.diskria.poetesse.java

import com.palantir.javapoet.AnnotationSpec
import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass

class JavaAnnotationConfigScope private constructor() {

    sealed interface External

    @PublishedApi internal interface Internal {

        fun append(annotation: JPAnnotation)

        companion object {
            internal fun of(
                append: (annotation: AnnotationSpec) -> Unit,
            ): Internal = object : Internal {
                override fun append(annotation: AnnotationSpec) = append(annotation)
            }
        }
    }
}

fun <A : Annotation> JavaAnnotationConfigScope.External.annotation(
    kClass: KClass<out A>,
    block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    internal.append(JavaAnnotationScope.of(kClass).apply(block).build())
}

inline fun <reified A : Annotation> JavaAnnotationConfigScope.External.annotation(
    noinline block: JavaAnnotationScope<A>.() -> Unit = {}
) {
    internal.append(JavaAnnotationScope.of<A>().apply(block).build())
}

fun JavaAnnotationConfigScope.External.annotation(
    className: XClassName,
    block: JavaAnnotationScope<Annotation>.() -> Unit = {}
) {
    internal.append(JavaAnnotationScope.of(className).apply(block).build())
}

@PublishedApi
internal val JavaAnnotationConfigScope.External.internal: JavaAnnotationConfigScope.Internal
    get() = when (this) {
        is JavaTypeScope -> annotationConfigInternalScope
    }
