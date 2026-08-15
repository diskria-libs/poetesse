package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.interop.XClassName

interface JavaFileFactory : PoetesseJavaScope

fun JavaFileFactory.file(packageName: String?, name: String, block: JavaFileScope.Block): PoetesseJavaFile =
    JavaFileScope.of(packageName, name).apply(block).build()

fun JavaFileFactory.file(className: XClassName, block: JavaFileScope.Block): PoetesseJavaFile =
    file(className.packageName, className.simpleName, block)
