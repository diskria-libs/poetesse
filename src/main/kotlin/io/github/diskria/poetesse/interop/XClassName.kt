package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.*
import io.github.diskria.poetesse.java.JPClassName
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
        KPClassName(packageName.orEmpty(), simpleNames).copy(nullable = isNullable) as KPClassName

    override val kotlinAsJava: JPClassName = JPClassName.get(
        packageName.orEmpty(),
        simpleNames.first(),
        *simpleNames.drop(1).toTypedArray()
    )

    override fun interopToKotlin(): KPClassName {
        val mapped = J2K[kotlinAsJava] ?: javaAsKotlin
        return if (isNullable) mapped.copy(nullable = true) as KPClassName else mapped
    }

    override fun interopToJava(): JPClassName {
        val key = javaAsKotlin.copy(nullable = false)
        return K2J[key] ?: kotlinAsJava
    }

    fun nested(name: String): XClassName =
        XClassName(packageName, simpleNames + name, false)

    companion object {
        private val K2J: Map<KPClassName, JPClassName> = mapOf(
            ANY to JPClassName.OBJECT,
            STRING to JPClassName.get(String::class.java),
            CHAR_SEQUENCE to JPClassName.get(CharSequence::class.java),
            COMPARABLE to JPClassName.get(Comparable::class.java),
            THROWABLE to JPClassName.get(Throwable::class.java),
            ANNOTATION to JPClassName.get(Annotation::class.java),
            NUMBER to JPClassName.get(Number::class.java),
            MUTABLE_ITERABLE to JPClassName.get(Iterable::class.java),
            ITERABLE to JPClassName.get(Iterable::class.java),
            MUTABLE_COLLECTION to JPClassName.get(Collection::class.java),
            COLLECTION to JPClassName.get(Collection::class.java),
            MUTABLE_LIST to JPClassName.get(List::class.java),
            LIST to JPClassName.get(List::class.java),
            MUTABLE_SET to JPClassName.get(Set::class.java),
            SET to JPClassName.get(Set::class.java),
            MUTABLE_MAP to JPClassName.get(Map::class.java),
            MAP to JPClassName.get(Map::class.java),
        )

        private val J2K: Map<JPClassName, KPClassName> =
            K2J.entries.associate { (k, j) -> j to k }

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
            of(kClass.asClassName().copy(nullable = isNullable) as KPClassName)

        inline fun <reified T : Any> of(isNullable: Boolean = false): XClassName =
            of(T::class, isNullable)
    }
}
