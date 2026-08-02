package io.github.diskria.poetesse.extensions

fun <T> Iterable<T>.joinWithTrailing(postfix: String, transform: (T) -> String = { it.toString() }): String =
    joinToString(separator = "") { "${transform(it)}$postfix" }
