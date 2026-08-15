package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.interop.XTypeName

interface KotlinTypeAliasFactory : PoetesseKotlinScope

fun KotlinTypeAliasFactory.typeAlias(name: String, type: XTypeName, block: KotlinTypeAliasScope.Block = {}) =
    KotlinTypeAliasRef(name) { KotlinTypeAliasScope.of(name, type).apply(block).build() }
