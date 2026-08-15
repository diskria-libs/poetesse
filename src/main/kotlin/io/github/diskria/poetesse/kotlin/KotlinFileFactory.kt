package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XClassName

interface KotlinFileFactory : PoetesseKotlinScope

fun KotlinFileFactory.file(packageName: String?, name: String, block: KotlinFileScope.Block = {}): PoetesseKotlinFile =
    KotlinFileScope.of(packageName, name).apply(block).build()

fun KotlinFileFactory.file(className: XClassName, block: KotlinFileScope.Block = {}): PoetesseKotlinFile =
    file(className.packageName, className.simpleName, block)
