package io.github.diskria.poetesse.extensions

import io.github.diskria.poetesse.kotlin.KPTypeName

@Suppress("UNCHECKED_CAST")
fun <T : KPTypeName> T.setNullable(nullable: Boolean): T =
    if (nullable == isNullable) this
    else copy(nullable = nullable) as T
