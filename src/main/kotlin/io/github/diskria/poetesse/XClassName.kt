package io.github.diskria.poetesse

class XClassName private constructor(
    val packageName: String?,
    private val simpleNames: List<String>,
) {
    internal val kotlin: com.squareup.kotlinpoet.ClassName =
        com.squareup.kotlinpoet.ClassName(packageName.orEmpty(), simpleNames)

    internal val java: com.palantir.javapoet.ClassName =
        com.palantir.javapoet.ClassName.get(
            packageName.orEmpty(),
            simpleNames.first(),
            *simpleNames.drop(1).toTypedArray()
        )

    val simpleName: String = simpleNames.last()
    val nestedName: String = simpleNames.joinToString(".")
    val qualifiedName: String = listOfNotNull(packageName, nestedName).joinToString(".")

    val outer: XClassName?
        get() = if (simpleNames.size == 1) null
        else XClassName(packageName, simpleNames.dropLast(1))

    fun inner(name: String): XClassName =
        XClassName(packageName, simpleNames + name)

    companion object {
        fun of(packageName: String?, vararg simpleNames: String): XClassName =
            XClassName(packageName, simpleNames.toList())
    }
}
