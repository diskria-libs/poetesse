package io.github.diskria.poetesse.java

class JavaDeferredAnnotation<A : Annotation> @PublishedApi internal constructor(buildSpec: () -> JPAnnotation) {
    @PublishedApi
    internal val spec: JPAnnotation by lazy(buildSpec)
}
