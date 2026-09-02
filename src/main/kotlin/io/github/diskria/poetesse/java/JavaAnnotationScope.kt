package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

class JavaAnnotationScope<A : Annotation> private constructor(
    override val config: Poetesse.Config,
    private val builder: JPAnnotationBuilder,
) : PoetesseJavaScope {

    internal typealias Block<A> = JavaAnnotationScope<A>.() -> Unit

    private typealias MemberProperty<A, V> = KProperty1<out A, V>
    private typealias MemberArrayProperty<A, E> = MemberProperty<A, Array<out E>>

    fun member(name: String, value: JPCodeBlock) {
        builder.addMember(name, value)
    }

    fun member(name: String, value: JavaCodeRef) {
        member(name, value.codeBlock)
    }

    fun member(name: String, block: JavaCodeScope.Block) {
        member(name, JavaCodeScope.of(block))
    }

    @JvmName("booleanArgument")
    fun member(property: MemberProperty<A, Boolean>, value: Boolean) {
        member(property.name) { L(value) }
    }

    @JvmName("booleanArrayArgument")
    fun member(property: MemberProperty<A, BooleanArray>, values: Iterable<Boolean>) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("booleanArrayArgument")
    fun member(property: MemberProperty<A, BooleanArray>, vararg values: Boolean) {
        member(property, values.asIterable())
    }

    @JvmName("byteArgument")
    fun member(property: MemberProperty<A, Byte>, value: Byte) {
        member(property.name) { L(value) }
    }

    @JvmName("byteArrayArgument")
    fun member(property: MemberProperty<A, ByteArray>, values: Iterable<Byte>) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("byteArrayArgument")
    fun member(property: MemberProperty<A, ByteArray>, vararg values: Byte) {
        member(property, values.asIterable())
    }

    @JvmName("shortArgument")
    fun member(property: MemberProperty<A, Short>, value: Short) {
        member(property.name) { L(value) }
    }

    @JvmName("shortArrayArgument")
    fun member(property: MemberProperty<A, ShortArray>, values: Iterable<Short>) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("shortArrayArgument")
    fun member(property: MemberProperty<A, ShortArray>, vararg values: Short) {
        member(property, values.asIterable())
    }

    @JvmName("intArgument")
    fun member(property: MemberProperty<A, Int>, value: Int) {
        member(property.name) { L(value) }
    }

    @JvmName("intArrayArgument")
    fun member(property: MemberProperty<A, IntArray>, values: Iterable<Int>) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("intArrayArgument")
    fun member(property: MemberProperty<A, IntArray>, vararg values: Int) {
        member(property, values.asIterable())
    }

    @JvmName("longArgument")
    fun member(property: MemberProperty<A, Long>, value: Long) {
        member(property.name) { L(value) }
    }

    @JvmName("longArrayArgument")
    fun member(property: MemberProperty<A, LongArray>, values: Iterable<Long>) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("longArrayArgument")
    fun member(property: MemberProperty<A, LongArray>, vararg values: Long) {
        member(property, values.asIterable())
    }

    @JvmName("charArgument")
    fun member(property: MemberProperty<A, Char>, value: Char) {
        member(property.name) { L(value) }
    }

    @JvmName("charArrayArgument")
    fun member(property: MemberProperty<A, CharArray>, values: Iterable<Char>) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("charArrayArgument")
    fun member(property: MemberProperty<A, CharArray>, vararg values: Char) {
        member(property, values.asIterable())
    }

    @JvmName("floatArgument")
    fun member(property: MemberProperty<A, Float>, value: Float) {
        member(property.name) { L(value) }
    }

    @JvmName("floatArrayArgument")
    fun member(property: MemberProperty<A, FloatArray>, values: Iterable<Float>) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("floatArrayArgument")
    fun member(property: MemberProperty<A, FloatArray>, vararg values: Float) {
        member(property, values.asIterable())
    }

    @JvmName("doubleArgument")
    fun member(property: MemberProperty<A, Double>, value: Double) {
        member(property.name) { L(value) }
    }

    @JvmName("doubleArrayArgument")
    fun member(property: MemberProperty<A, DoubleArray>, values: Iterable<Double>) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("doubleArrayArgument")
    fun member(property: MemberProperty<A, DoubleArray>, vararg values: Double) {
        member(property, values.asIterable())
    }

    @JvmName("stringArgument")
    fun member(property: MemberProperty<A, String>, value: String) {
        member(property.name) { S(value) }
    }

    @JvmName("stringArrayArgument")
    fun member(property: MemberArrayProperty<A, String>, values: Iterable<String>) {
        member(property.name) {
            values.joinToArray { S(it) }
        }
    }

    @JvmName("stringArrayArgument")
    fun member(property: MemberArrayProperty<A, String>, vararg values: String) {
        member(property, values.asIterable())
    }

    @JvmName("classArgument")
    fun member(property: MemberProperty<A, KClass<*>>, value: KClass<*>) {
        member(property.name) { "${T(value)}.class" }
    }

    @JvmName("classArrayArgument")
    fun member(property: MemberArrayProperty<A, KClass<*>>, values: Iterable<KClass<*>>) {
        member(property.name) {
            values.joinToArray { "${T(it)}.class" }
        }
    }

    @JvmName("classArrayArgument")
    fun member(property: MemberArrayProperty<A, KClass<*>>, vararg values: KClass<*>) {
        member(property, values.asIterable())
    }

    @JvmName("xTypeArgument")
    fun member(property: MemberProperty<A, KClass<*>>, value: XTypeName) {
        member(property.name) { "${T(value)}.class" }
    }

    @JvmName("xTypeArrayArgument")
    fun member(property: MemberArrayProperty<A, KClass<*>>, values: Iterable<XTypeName>) {
        member(property.name) {
            values.joinToArray { "${T(it)}.class" }
        }
    }

    @JvmName("classNameArrayArgument")
    fun member(property: MemberArrayProperty<A, KClass<*>>, vararg values: XTypeName) {
        member(property, values.asIterable())
    }

    @JvmName("enumArgument")
    inline fun <reified E : Enum<E>> member(property: MemberProperty<A, E>, value: E) {
        member(property.name) { "${T<E>()}.${L(value.name)}" }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> member(property: MemberArrayProperty<A, E>, values: Iterable<E>) {
        member(property.name) {
            values.joinToArray { "${T<E>()}.${L(it.name)}" }
        }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> member(property: MemberArrayProperty<A, E>, vararg values: E) {
        member(property, values.asIterable())
    }

    @JvmName("annotationArgument")
    inline fun <reified Nested : Annotation> member(
        property: MemberProperty<A, Nested>,
        annotation: JavaTypedAnnotationRef<Nested>,
    ) {
        member(property.name) { L(annotation) }
    }

    @JvmName("annotationArgument")
    inline fun <reified Nested : Annotation> member(
        property: MemberProperty<A, Nested>,
        noinline block: Block<Nested> = {}
    ) {
        member(property, JavaTypedAnnotationRef {
            of<Nested>(xClass<Nested>()).apply(block).build()
        })
    }

    @JvmName("annotationArrayArgument")
    inline fun <reified Nested : Annotation> member(
        property: MemberArrayProperty<A, Nested>,
        values: Iterable<JavaTypedAnnotationRef<Nested>>
    ) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("annotationArrayArgument")
    inline fun <reified Nested : Annotation> member(
        property: MemberArrayProperty<A, Nested>,
        vararg values: JavaTypedAnnotationRef<Nested>
    ) {
        member(property, values.asIterable())
    }

    @PublishedApi
    internal fun build() = builder.build()

    @PublishedApi
    internal companion object {
        context(poetesse: PoetesseScope)
        fun <A : Annotation> of(className: XClassName) = JavaAnnotationScope<A>(
            config = poetesse.config,
            builder = JPAnnotation.builder(className.interopToJava(resolveNullability = false)),
        )
    }
}

@PublishedApi
internal inline fun <reified E> Iterable<E>.joinToArray(crossinline transform: (E) -> String): String =
    joinToString(prefix = "{", postfix = "}") { transform(it) }
