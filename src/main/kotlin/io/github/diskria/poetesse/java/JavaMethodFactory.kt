package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.LazyDelegate

interface JavaMethodFactory : PoetesseJavaScope

fun JavaMethodFactory.method(name: String, block: JavaMethodScope.Block = {}) =
    JavaMethodRef(name) { JavaMethodScope.of(name).apply(block).build() }

fun JavaMethodFactory.method(block: JavaMethodScope.Block = {}) =
    LazyDelegate { method(it, block) }
