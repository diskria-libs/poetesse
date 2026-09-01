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

    private typealias ArgumentProperty<A, V> = KProperty1<out A, V>
    private typealias ArrayArgumentProperty<A, E> = ArgumentProperty<A, Array<out E>>

    fun member(name: String, value: JPCodeBlock) {
        builder.addMember(name, value)
    }

    fun member(name: String, value: JavaCodeRef) {
        member(name, value.codeBlock)
    }

    fun member(name: String, block: JavaCodeScope.Block) {
        member(name, JavaCodeScope.of(block))
    }

    @JvmName("stringArgument")
    fun member(property: ArgumentProperty<A, String>, value: String) {
        member(property.name) { S(value) }
    }

    @JvmName("stringArrayArgument")
    fun member(property: ArrayArgumentProperty<A, String>, values: Iterable<String>) {
        member(property.name) {
            values.joinToArray { S(it) }
        }
    }

    @JvmName("stringArrayArgument")
    fun member(property: ArrayArgumentProperty<A, String>, vararg values: String) {
        member(property, values.asIterable())
    }

    @JvmName("booleanArgument")
    fun member(property: ArgumentProperty<A, Boolean>, value: Boolean) {
        member(property.name) { L(value) }
    }

    @JvmName("booleanArrayArgument")
    fun member(property: ArgumentProperty<A, BooleanArray>, values: Iterable<Boolean>) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("booleanArrayArgument")
    fun member(property: ArgumentProperty<A, BooleanArray>, vararg values: Boolean) {
        member(property, values.asIterable())
    }

    @JvmName("intArgument")
    fun member(property: ArgumentProperty<A, Int>, value: Int) {
        member(property.name) { L(value) }
    }

    @JvmName("intArrayArgument")
    fun member(property: ArgumentProperty<A, IntArray>, values: Iterable<Int>) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("intArrayArgument")
    fun member(property: ArgumentProperty<A, IntArray>, vararg values: Int) {
        member(property, values.asIterable())
    }

    @JvmName("classArgument")
    fun member(property: ArgumentProperty<A, KClass<*>>, value: KClass<*>) {
        member(property.name) { "${T(value)}.class" }
    }

    @JvmName("classArrayArgument")
    fun member(property: ArrayArgumentProperty<A, KClass<*>>, values: Iterable<KClass<*>>) {
        member(property.name) {
            values.joinToArray { "${T(it)}.class" }
        }
    }

    @JvmName("classArrayArgument")
    fun member(property: ArrayArgumentProperty<A, KClass<*>>, vararg values: KClass<*>) {
        member(property, values.asIterable())
    }

    @JvmName("xTypeArgument")
    fun member(property: ArgumentProperty<A, KClass<*>>, value: XTypeName) {
        member(property.name) { "${T(value)}.class" }
    }

    @JvmName("xTypeArrayArgument")
    fun member(property: ArrayArgumentProperty<A, KClass<*>>, values: Iterable<XTypeName>) {
        member(property.name) {
            values.joinToArray { "${T(it)}.class" }
        }
    }

    @JvmName("classNameArrayArgument")
    fun member(property: ArrayArgumentProperty<A, KClass<*>>, vararg values: XTypeName) {
        member(property, values.asIterable())
    }

    @JvmName("enumArgument")
    inline fun <reified E : Enum<E>> member(property: ArgumentProperty<A, E>, value: E) {
        member(property.name) { "${T<E>()}.${L(value.name)}" }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> member(property: ArrayArgumentProperty<A, E>, values: Iterable<E>) {
        member(property.name) {
            values.joinToArray { "${T<E>()}.${L(it.name)}" }
        }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> member(property: ArrayArgumentProperty<A, E>, vararg values: E) {
        member(property, values.asIterable())
    }

    @JvmName("annotationArgument")
    inline fun <reified Nested : Annotation> member(
        property: ArgumentProperty<A, Nested>,
        annotation: JavaTypedAnnotationRef<Nested>,
    ) {
        member(property.name) { L(annotation) }
    }

    @JvmName("annotationArgument")
    inline fun <reified Nested : Annotation> member(
        property: ArgumentProperty<A, Nested>,
        noinline block: Block<Nested> = {}
    ) {
        member(property, JavaTypedAnnotationRef {
            of<Nested>(xClass<Nested>()).apply(block).build()
        })
    }

    @JvmName("annotationArrayArgument")
    inline fun <reified Nested : Annotation> member(
        property: ArrayArgumentProperty<A, Nested>,
        values: Iterable<JavaTypedAnnotationRef<Nested>>
    ) {
        member(property.name) {
            values.joinToArray { L(it) }
        }
    }

    @JvmName("annotationArrayArgument")
    inline fun <reified Nested : Annotation> member(
        property: ArrayArgumentProperty<A, Nested>,
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
