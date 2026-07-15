package io.github.diskria.poetesse.java

import com.palantir.javapoet.AnnotationSpec
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

@PoetesseJava
class JavaAnnotationScope<A : Annotation> @PublishedApi internal constructor(
    @PublishedApi internal val specBuilder: AnnotationSpec.Builder
) {
    private typealias ArgumentProperty<A, V> = KProperty1<out A, V>
    private typealias ArrayArgumentProperty<A, E> = ArgumentProperty<A, Array<out E>>

    fun argument(name: String, statement: JavaStatementBuilder) {
        specBuilder.addMember(name, JavaStatementScope.create(statement))
    }

    fun argument(property: ArgumentProperty<A, *>, statement: JavaStatementBuilder) {
        argument(property.name, statement)
    }

    fun argument(property: ArgumentProperty<A, String>, value: String) {
        argument(property.name) { S(value) }
    }

    @JvmName("stringArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, String>, values: Iterable<String>) {
        argument(property.name) {
            values.joinToArray { S(it) }
        }
    }

    @JvmName("stringArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, String>, vararg values: String) {
        argument(property, values.asIterable())
    }

    fun argument(property: ArgumentProperty<A, Boolean>, value: Boolean) {
        argument(property.name) { L(value) }
    }

    @JvmName("booleanArrayArgument")
    fun argument(property: ArgumentProperty<A, BooleanArray>, values: Iterable<Boolean>) {
        argument(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("booleanArrayArgument")
    fun argument(property: ArgumentProperty<A, BooleanArray>, vararg values: Boolean) {
        argument(property, values.asIterable())
    }

    fun argument(property: ArgumentProperty<A, Int>, value: Int) {
        argument(property.name) { L(value) }
    }

    @JvmName("intArrayArgument")
    fun argument(property: ArgumentProperty<A, IntArray>, values: Iterable<Int>) {
        argument(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("intArrayArgument")
    fun argument(property: ArgumentProperty<A, IntArray>, vararg values: Int) {
        argument(property, values.asIterable())
    }

    fun argument(property: ArgumentProperty<A, KClass<*>>, value: KClass<*>) {
        argument(property.name) { classRef(value) }
    }

    @JvmName("classArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, values: Iterable<KClass<*>>) {
        argument(property.name) {
            values.joinToArray { classRef(it) }
        }
    }

    @JvmName("classArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, vararg values: KClass<*>) {
        argument(property, values.asIterable())
    }

    fun argument(property: ArgumentProperty<A, KClass<*>>, value: XClassName) {
        argument(property.name) { classRef(value) }
    }

    @JvmName("classNameArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, values: Iterable<XClassName>) {
        argument(property.name) {
            values.joinToArray { classRef(it) }
        }
    }

    @JvmName("classNameArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, vararg values: XClassName) {
        argument(property, values.asIterable())
    }

    inline fun <reified E : Enum<E>> argument(property: ArgumentProperty<A, E>, value: E) {
        argument(property.name) { enumEntryRef<E>(value) }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> argument(property: ArrayArgumentProperty<A, E>, values: Iterable<E>) {
        argument(property.name) {
            values.joinToArray { enumEntryRef<E>(it) }
        }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> argument(property: ArrayArgumentProperty<A, E>, vararg values: E) {
        argument(property, values.asIterable())
    }

    @PublishedApi
    internal fun build(): AnnotationSpec =
        specBuilder.build()

    @PublishedApi
    internal companion object {
        fun <A : Annotation> of(kClass: KClass<out A>): JavaAnnotationScope<A> =
            JavaAnnotationScope(AnnotationSpec.builder(kClass.java))

        inline fun <reified A : Annotation> of(): JavaAnnotationScope<A> =
            JavaAnnotationScope(AnnotationSpec.builder(A::class.java))

        fun of(className: XClassName): JavaAnnotationScope<Annotation> =
            JavaAnnotationScope(AnnotationSpec.builder(className.java))
    }
}

@PublishedApi
internal inline fun <reified E> Iterable<E>.joinToArray(crossinline transform: (E) -> String): String =
    joinToString(prefix = "{", postfix = "}") { transform(it) }

internal fun JavaStatementScope.classRef(value: XClassName): String =
    "${T(value)}.class"

internal fun JavaStatementScope.classRef(value: KClass<*>): String =
    "${T(value)}.class"

@PublishedApi
internal inline fun <reified E : Enum<E>> JavaStatementScope.enumEntryRef(value: E): String =
    "${T(E::class)}.${L(value.name)}"
