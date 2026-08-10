package io.github.diskria.poetesse.extensions

fun String.capitalized(): String =
    replaceFirstChar {
        if (it.isLowerCase()) it.titlecase()
        else it.toString()
    }
