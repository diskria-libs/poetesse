package io.github.diskria.poetesse.java

class JavaDeferredAnnotation<A : Annotation> internal constructor(buildSpec: () -> JPAnnotation) {
    @PublishedApi
    internal val spec: JPAnnotation by lazy(buildSpec)
}
