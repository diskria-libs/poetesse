package io.github.diskria.poetesse.java

class JavaDeferredTypedAnnotation<A : Annotation> @PublishedApi internal constructor(buildSpec: () -> JPAnnotation) {
    internal val spec: JPAnnotation by lazy(buildSpec)
}

typealias JavaDeferredAnnotation = JavaDeferredTypedAnnotation<*>
