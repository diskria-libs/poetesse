package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.asJPClassName
import io.github.diskria.poetesse.extensions.asKPClassName
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.kotlin.KPClassName
import kotlin.reflect.KClass

class XClassName private constructor(
    config: Poetesse.Config,
    val packageName: String?,
    val simpleNames: List<String>,
    override val isNullable: Boolean,
) : XTypedTypeName<KPClassName, JPClassName>(config) {

    internal typealias Factory = (name: String) -> XClassName

    val simpleName: String = simpleNames.last()
    val nestedName: String = simpleNames.joinToString(".")
    val qualifiedName: String = buildString {
        packageName?.let { append("$it.") }
        append(nestedName)
    }

    private val rawKotlin: KPClassName =
        KPClassName(packageName.orEmpty(), simpleNames)

    private val rawJava: JPClassName =
        JPClassName.get(packageName.orEmpty(), simpleNames.first(), *simpleNames.drop(1).toTypedArray())

    override fun interopToKotlinInternal(): KPClassName =
        javaToKotlin[rawJava] ?: rawKotlin

    override fun interopToJavaInternal(): JPClassName =
        kotlinToJava[rawKotlin] ?: rawJava

    internal fun nested(name: String): XClassName =
        of(packageName, (simpleNames + name), false)

    internal companion object {
        private val kotlinToJava = buildMap {
            // Order and structure reflect org.jetbrains.kotlin.metadata.jvm.deserialization.ClassMapperLite,
            // adapted for Poet classes instead of JVM descriptors.
            sequenceOf(
                Any::class, Nothing::class, Annotation::class,

                String::class, CharSequence::class, Throwable::class, Cloneable::class, Number::class,
                Comparable::class, Enum::class,
            ).forEach { put(it.asKPClassName(), it.asJPClassName()) }

            sequenceOf(
                Iterator::class, Collection::class, List::class, Set::class, Map::class, ListIterator::class,
                Iterable::class, Map.Entry::class
            ).forEach { kClass ->
                val kpClassName = kClass.asKPClassName()
                val jpClassName = kClass.asJPClassName()
                put(kpClassName, jpClassName)
                put(KPClassName(kpClassName.packageName, kpClassName.simpleNames.map { "Mutable$it" }), jpClassName)
            }

            sequenceOf(
                Boolean.Companion::class, Byte.Companion::class, Short.Companion::class, Int.Companion::class,
                Long.Companion::class, Char.Companion::class, Float.Companion::class, Double.Companion::class,
                Enum.Companion::class, String.Companion::class,
            ).forEach { put(it.asKPClassName(), it.asJPClassName()) }

            // Typealiases defined in Kotlin 2.3.20 stdlib.
            // Using `::class` resolves them to the target Java class, losing the original Kotlin FQCN.
            // We explicitly reconstruct KPClassName using explicit packages.
            @Suppress("RemoveRedundantQualifierName")
            mapOf(
                // jvmMain/kotlin
                "kotlin" to arrayOf(
                    // TypeAliases.kt
                    kotlin.Error::class,
                    kotlin.Exception::class,
                    kotlin.RuntimeException::class,
                    kotlin.IllegalArgumentException::class,
                    kotlin.IllegalStateException::class,
                    kotlin.IndexOutOfBoundsException::class,
                    kotlin.UnsupportedOperationException::class,
                    kotlin.ArithmeticException::class,
                    kotlin.NumberFormatException::class,
                    kotlin.NullPointerException::class,
                    kotlin.ClassCastException::class,
                    kotlin.AssertionError::class,
                    kotlin.NoSuchElementException::class,
                    kotlin.ConcurrentModificationException::class,
                    kotlin.Comparator::class,

                    // AutoCloseableJVM.kt
                    kotlin.AutoCloseable::class,
                ),
                // jvmMain/kotlin/collections/TypeAliases.kt
                "kotlin.collections" to arrayOf(
                    kotlin.collections.RandomAccess::class,
                    kotlin.collections.ArrayList::class,
                    kotlin.collections.LinkedHashMap::class,
                    kotlin.collections.HashMap::class,
                    kotlin.collections.LinkedHashSet::class,
                    kotlin.collections.HashSet::class,
                ),
                // jvmMain/kotlin/text/TypeAliases.kt
                "kotlin.text" to arrayOf(
                    kotlin.text.Appendable::class,
                    kotlin.text.StringBuilder::class,
                    kotlin.text.CharacterCodingException::class,
                ),
                // jvmMain/kotlin/coroutines/cancellation/CancellationException.kt
                "kotlin.coroutines.cancellation" to arrayOf(
                    kotlin.coroutines.cancellation.CancellationException::class,
                ),
            ).forEach { (packageName, typeAliases) ->
                typeAliases.forEach {
                    val jpClassName = it.asJPClassName()
                    put(KPClassName(packageName, jpClassName.simpleNames()), jpClassName)
                }
            }
        }
        private val javaToKotlin = kotlinToJava.entries.associate { (k, j) -> j to k }

        context(poetesse: PoetesseScope)
        fun of(packageName: String?, simpleNames: List<String>, isNullable: Boolean = false) =
            XClassName(poetesse.config, packageName?.ifEmpty { null }, simpleNames, isNullable)

        context(poetesse: PoetesseScope)
        fun of(packageName: String?, simpleName: String, isNullable: Boolean = false) =
            of(packageName, listOf(simpleName), isNullable)
    }
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPClassName.asXClassName() =
    XClassName.of(packageName.ifEmpty { null }, simpleNames, isNullable)

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPClassName.asXClassName(nullable: Boolean) =
    XClassName.of(packageName().ifEmpty { null }, simpleNames(), nullable)

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KClass<*>.toXClass(nullable: Boolean): XClassName {
    require(this != Array::class && !java.isArray) {
        val className = simpleName ?: this.toString()
        val lines = listOf(
            "xType<Int>().array()" to "IntArray / int[]",
            "xType<Int>(boxed = true).array()" to "Array<Int> / Integer[]",
            "xType<Int?>().array()" to "Array<Int?> / @Nullable Integer[]",
        )
        val maxLength = lines.maxOf { it.first.length }
        buildString {
            appendLine("Cannot create XClassName directly from array type '$className'.")
            appendLine()
            appendLine("Expected factory methods:")
            lines.forEach { (left, right) -> appendLine("${left.padEnd(maxLength)} => $right".prependIndent("  ")) }
        }
    }
    return with(poetesse) { xClass(asClassName(), nullable) }
}
