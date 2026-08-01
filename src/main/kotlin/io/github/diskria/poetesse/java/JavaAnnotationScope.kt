package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XClassName
import io.github.diskria.poetesse.interop.XTypeName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

@PoetesseJava
class JavaAnnotationScope<A : Annotation> internal constructor(
    private val specBuilder: JPAnnotationBuilder
) {
    private typealias ArgumentProperty<A, V> = KProperty1<out A, V>
    private typealias ArrayArgumentProperty<A, E> = ArgumentProperty<A, Array<out E>>

    fun argument(name: String, deferredCode: JavaCodeRef) {
        specBuilder.addMember(name, deferredCode.codeBlock)
    }

    fun argument(name: String, value: JavaCodeBuilder) {
        argument(name, JavaCodeScope.of(value))
    }

    @JvmName("propertyArgument")
    fun argument(property: ArgumentProperty<A, *>, value: JavaCodeBuilder) {
        argument(property.name, value)
    }

    @JvmName("stringArgument")
    fun argument(property: ArgumentProperty<A, String>, value: String) {
        argument(property) { S(value) }
    }

    @JvmName("stringArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, String>, values: Iterable<String>) {
        argument(property) {
            expression.arrayOf(values) { S(it) }
        }
    }

    @JvmName("stringArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, String>, vararg values: String) {
        argument(property, values.asIterable())
    }

    @JvmName("booleanArgument")
    fun argument(property: ArgumentProperty<A, Boolean>, value: Boolean) {
        argument(property) { L(value) }
    }

    @JvmName("booleanArrayArgument")
    fun argument(property: ArgumentProperty<A, BooleanArray>, values: Iterable<Boolean>) {
        argument(property) {
            expression.arrayOf(values) { L(it) }
        }
    }

    @JvmName("booleanArrayArgument")
    fun argument(property: ArgumentProperty<A, BooleanArray>, vararg values: Boolean) {
        argument(property, values.asIterable())
    }

    @JvmName("intArgument")
    fun argument(property: ArgumentProperty<A, Int>, value: Int) {
        argument(property) { L(value) }
    }

    @JvmName("intArrayArgument")
    fun argument(property: ArgumentProperty<A, IntArray>, values: Iterable<Int>) {
        argument(property) {
            expression.arrayOf(values) { L(it) }
        }
    }

    @JvmName("intArrayArgument")
    fun argument(property: ArgumentProperty<A, IntArray>, vararg values: Int) {
        argument(property, values.asIterable())
    }

    @JvmName("classArgument")
    fun argument(property: ArgumentProperty<A, KClass<*>>, value: KClass<*>) {
        argument(property) { expression.class_(value) }
    }

    @JvmName("classArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, values: Iterable<KClass<*>>) {
        argument(property) {
            expression.arrayOf(values) { expression.class_(it) }
        }
    }

    @JvmName("classArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, vararg values: KClass<*>) {
        argument(property, values.asIterable())
    }

    @JvmName("classNameArgument")
    fun argument(property: ArgumentProperty<A, KClass<*>>, value: XTypeName) {
        argument(property) { expression.class_(value) }
    }

    @JvmName("classNameArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, values: Iterable<XTypeName>) {
        argument(property) {
            expression.arrayOf(values) { expression.class_(it) }
        }
    }

    @JvmName("classNameArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, vararg values: XTypeName) {
        argument(property, values.asIterable())
    }

    @JvmName("enumArgument")
    inline fun <reified E : Enum<E>> argument(property: ArgumentProperty<A, E>, value: E) {
        argument(property) { expression.enumEntry(value) }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> argument(property: ArrayArgumentProperty<A, E>, values: Iterable<E>) {
        argument(property) {
            expression.arrayOf(values) { expression.enumEntry(it) }
        }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> argument(property: ArrayArgumentProperty<A, E>, vararg values: E) {
        argument(property, values.asIterable())
    }

    @JvmName("annotationArgument")
    inline fun <reified Embedded : Annotation> argument(
        property: ArgumentProperty<A, Embedded>,
        annotation: JavaTypedAnnotationRef<Embedded>,
    ) {
        argument(property) { L(annotation) }
    }

    @JvmName("annotationArgument")
    inline fun <reified Embedded : Annotation> argument(
        property: ArgumentProperty<A, Embedded>,
        noinline block: JavaAnnotationScope<Embedded>.() -> Unit = {}
    ) {
        argument(property, JavaTypedAnnotationRef { of<Embedded>().apply(block).build() })
    }

    @JvmName("annotationArrayArgument")
    inline fun <reified Embedded : Annotation> argument(
        property: ArrayArgumentProperty<A, Embedded>,
        values: Iterable<JavaTypedAnnotationRef<Embedded>>
    ) {
        argument(property.name) {
            expression.arrayOf(values) { L(it) }
        }
    }

    @JvmName("annotationArrayArgument")
    inline fun <reified Embedded : Annotation> argument(
        property: ArrayArgumentProperty<A, Embedded>,
        vararg values: JavaTypedAnnotationRef<Embedded>
    ) {
        argument(property, values.asIterable())
    }

    @PublishedApi
    internal fun build(): JPAnnotation =
        specBuilder.build()

    @PublishedApi
    internal companion object {
        fun <A : Annotation> of(className: XClassName): JavaAnnotationScope<A> =
            JavaAnnotationScope(JPAnnotation.builder(className.interopToJava()))

        fun <A : Annotation> of(kClass: KClass<out A>): JavaAnnotationScope<A> =
            of(XClassName.of(kClass))

        inline fun <reified A : Annotation> of(): JavaAnnotationScope<A> =
            of(A::class)
    }
}
