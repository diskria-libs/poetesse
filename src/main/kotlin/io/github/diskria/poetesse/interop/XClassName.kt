package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.asClassName
import io.github.diskria.poetesse.extensions.asJPClassName
import io.github.diskria.poetesse.extensions.asKPClassName
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.java.JPArrayTypeName
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.kotlin.KPClassName
import kotlin.reflect.KClass

class XClassName private constructor(
    val packageName: String?,
    val simpleNames: List<String>,
    override val isNullable: Boolean,
) : XTypeName {

    val simpleName: String = simpleNames.last()
    val nestedName: String = simpleNames.joinToString(".")
    val qualifiedName: String = listOfNotNull(packageName, nestedName).joinToString(".")

    override val javaAsKotlin: KPClassName =
        KPClassName(packageName.orEmpty(), simpleNames).setNullable(isNullable)

    override val kotlinAsJava: JPClassName = JPClassName.get(
        packageName.orEmpty(),
        simpleNames.first(),
        *simpleNames.drop(1).toTypedArray()
    )

    override fun interopToKotlin(): KPClassName {
        val kpClassName = J2K[kotlinAsJava] ?: javaAsKotlin
        return kpClassName.setNullable(isNullable)
    }

    override fun interopToJava(): JPTypeName {
        val kpClassName = javaAsKotlin.setNullable(false)
        XPrimitiveKind.entries.forEach {
            if (it == XPrimitiveKind.VOID) return@forEach
            if (kpClassName == it.kotlin.peerClass(it.kotlin.simpleName + "Array")) {
                return JPArrayTypeName.of(if (isNullable) it.java.box() else it.java)
            }
        }
        return K2J[kpClassName] ?: kotlinAsJava
    }

    fun nested(name: String): XClassName =
        XClassName(packageName, simpleNames + name, false)

    companion object {
        private val K2J: Map<KPClassName, JPClassName> = buildMap {
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
                val kp = kClass.asKPClassName()
                val jp = kClass.asJPClassName()
                put(kp, jp)
                put(KPClassName(kp.packageName, kp.simpleNames.map { "Mutable$it" }), jp)
            }

            (0..22).forEach { i ->
                val name = "Function$i"
                put(KPClassName("kotlin", name), JPClassName.get("kotlin.jvm.functions", name))
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
                typeAliases.forEach { put(KPClassName(packageName, it.java.simpleName), it.asJPClassName()) }
            }
        }

        private val J2K: Map<JPClassName, KPClassName> = K2J.entries.associate { (k, j) -> j to k }

        fun of(packageName: String?, isNullable: Boolean = false, vararg simpleNames: String): XClassName =
            XClassName(packageName, simpleNames.toList(), isNullable)

        fun of(kotlin: KPClassName): XClassName =
            of(
                kotlin.packageName.takeIf { it.isNotEmpty() },
                kotlin.isNullable,
                *kotlin.simpleNames.toTypedArray(),
            )

        fun of(java: JPClassName): XClassName =
            of(
                java.packageName(),
                false,
                *java.simpleNames().toTypedArray(),
            )

        fun of(kClass: KClass<out Any>, isNullable: Boolean = false): XClassName =
            of(kClass.asClassName().setNullable(isNullable))

        inline fun <reified T : Any> of(isNullable: Boolean = false): XClassName =
            of(T::class, isNullable)
    }
}
