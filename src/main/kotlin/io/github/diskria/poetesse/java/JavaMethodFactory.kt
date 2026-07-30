package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.LazyDelegate

interface JavaMethodFactory

fun JavaMethodFactory.method(name: String, block: JavaMethodScope.() -> Unit = {}): JavaDeferredMethod =
    JavaDeferredMethod(name) { JavaMethodScope.of(name).apply(block).build() }

fun JavaMethodFactory.method(block: JavaMethodScope.() -> Unit = {}): LazyDelegate<JavaDeferredMethod> =
    LazyDelegate { name -> method(name, block) }
