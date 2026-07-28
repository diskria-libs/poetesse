package io.github.diskria.poetesse.java

import com.palantir.javapoet.AnnotationSpec

class JavaDeferredAnnotation<A : Annotation>(@PublishedApi internal val spec: AnnotationSpec)
