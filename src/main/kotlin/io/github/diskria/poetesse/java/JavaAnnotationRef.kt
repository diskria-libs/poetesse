package io.github.diskria.poetesse.java

typealias JavaAnnotationRef = JavaTypedAnnotationRef<*>

class JavaTypedAnnotationRef<A : Annotation> @PublishedApi internal constructor(buildSpec: () -> JPAnnotation) {
    internal val spec: JPAnnotation by lazy(buildSpec)
}
