package io.github.diskria.poetesse.utils

internal class StringAffix(val prefix: String = "", val suffix: String = "") {

    fun wrap(value: String): String =
        "$prefix$value$suffix"

    fun matches(value: String): Boolean =
        value.startsWith(prefix) && value.endsWith(suffix)

    fun unwrapOrNull(value: String): String? =
        if (!matches(value)) null
        else value.removePrefix(prefix).removeSuffix(suffix)
}
