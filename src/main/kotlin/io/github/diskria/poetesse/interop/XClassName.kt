package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.*
import io.github.diskria.poetesse.extensions.setNullable
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
        KPClassName(packageName.orEmpty(), simpleNames).setNullable(isNullable)

    override val kotlinAsJava: JPClassName = JPClassName.get(
        packageName.orEmpty(),
        simpleNames.first(),
        *simpleNames.drop(1).toTypedArray()
    )

    override fun interopToKotlin(): KPClassName {
        val mapped = J2K[kotlinAsJava] ?: javaAsKotlin
        return mapped.setNullable(isNullable)
    }

    override fun interopToJava(): JPClassName {
        val key = javaAsKotlin.setNullable(false)
        return K2J[key] ?: kotlinAsJava
    }

    fun nested(name: String): XClassName =
        XClassName(packageName, simpleNames + name, false)

    companion object {
        private val K2J: Map<KPClassName, JPClassName> = listOf(
            ANY to Object::class,
            STRING to String::class,
            CHAR_SEQUENCE to CharSequence::class,
            COMPARABLE to Comparable::class,
            THROWABLE to Throwable::class,
            ANNOTATION to Annotation::class,
            NUMBER to Number::class,
            MUTABLE_ITERABLE to MutableIterable::class,
            ITERABLE to Iterable::class,
            MUTABLE_COLLECTION to Collection::class,
            COLLECTION to Collection::class,
            MUTABLE_LIST to List::class,
            LIST to List::class,
            MUTABLE_SET to MutableSet::class,
            SET to Set::class,
            MUTABLE_MAP to Map::class,
            MAP to Map::class,
        ).associate { (className, kClass) -> className to JPClassName.get(kClass.java) }

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
            of(kClass.asClassName().setNullable(isNullable))

        inline fun <reified T : Any> of(isNullable: Boolean = false): XClassName =
            of(T::class, isNullable)
    }
}
