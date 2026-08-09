package io.github.diskria.poetesse.kotlin

typealias KotlinAnnotationRef = KotlinTypedAnnotationRef<*>

class KotlinTypedAnnotationRef<A : Annotation> @PublishedApi internal constructor(build: () -> KPAnnotation) {
    internal val spec: KPAnnotation by lazy(build)
}
