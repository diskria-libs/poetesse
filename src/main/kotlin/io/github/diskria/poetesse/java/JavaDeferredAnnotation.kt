package io.github.diskria.poetesse.java

typealias JavaDeferredAnnotation = JavaDeferredTypedAnnotation<*>

class JavaDeferredTypedAnnotation<A : Annotation> @PublishedApi internal constructor(buildSpec: () -> JPAnnotation) {
    internal val spec: JPAnnotation by lazy(buildSpec)
}
