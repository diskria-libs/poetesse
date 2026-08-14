package io.github.diskria.poetesse.java

class JavaConstructorRef internal constructor(build: () -> JPMethod) {
    internal val spec: JPMethod by lazy(build)
}
