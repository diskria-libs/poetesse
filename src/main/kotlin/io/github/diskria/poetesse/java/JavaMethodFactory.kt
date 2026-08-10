package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.LazyDelegate
import io.github.diskria.poetesse.PoetesseScope

interface JavaMethodFactory : PoetesseScope

fun JavaMethodFactory.method(name: String, block: JavaMethodScope.() -> Unit = {}): JavaMethodRef =
    JavaMethodRef(name) { JavaMethodScope.of(settings, name).apply(block).build() }

fun JavaMethodFactory.method(block: JavaMethodScope.() -> Unit = {}): LazyDelegate<JavaMethodRef> =
    LazyDelegate { name -> method(name, block) }
