package io.github.diskria.poetesse.java

import com.palantir.javapoet.AnnotationSpec
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

@PoetesseJava
class JavaAnnotationScope<A : Annotation> internal constructor(
    private val specBuilder: AnnotationSpec.Builder
) {
    private typealias ArgumentProperty<A, V> = KProperty1<out A, V>
    private typealias ArrayArgumentProperty<A, E> = ArgumentProperty<A, Array<out E>>

    fun argument(name: String, deferredCode: JavaDeferredCode) {
        specBuilder.addMember(name, deferredCode.codeBlock)
    }

    fun argument(name: String, buildValueCode: JavaCodeBuilder) {
        argument(name, JavaCodeScope.of(buildValueCode))
    }

    @JvmName("propertyArgument")
    fun argument(property: ArgumentProperty<A, *>, buildValueCode: JavaCodeBuilder) {
        argument(property.name, buildValueCode)
    }

    @JvmName("stringArgument")
    fun argument(property: ArgumentProperty<A, String>, value: String) {
        argument(property) { S(value) }
    }

    @JvmName("stringArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, String>, values: Iterable<String>) {
        argument(property) {
            arrayOf_(values) { S(it) }
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
            arrayOf_(values) { L(it) }
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
            arrayOf_(values) { L(it) }
        }
    }

    @JvmName("intArrayArgument")
    fun argument(property: ArgumentProperty<A, IntArray>, vararg values: Int) {
        argument(property, values.asIterable())
    }

    @JvmName("classArgument")
    fun argument(property: ArgumentProperty<A, KClass<*>>, value: KClass<*>) {
        argument(property) { classReference(value) }
    }

    @JvmName("classArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, values: Iterable<KClass<*>>) {
        argument(property) {
            arrayOf_(values) { classReference(it) }
        }
    }

    @JvmName("classArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, vararg values: KClass<*>) {
        argument(property, values.asIterable())
    }

    @JvmName("classNameArgument")
    fun argument(property: ArgumentProperty<A, KClass<*>>, value: XClassName) {
        argument(property) { classReference(value) }
    }

    @JvmName("classNameArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, values: Iterable<XClassName>) {
        argument(property) {
            arrayOf_(values) { classReference(it) }
        }
    }

    @JvmName("classNameArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, vararg values: XClassName) {
        argument(property, values.asIterable())
    }

    @JvmName("enumArgument")
    inline fun <reified E : Enum<E>> argument(property: ArgumentProperty<A, E>, value: E) {
        argument(property) { enumEntryReference(value) }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> argument(property: ArrayArgumentProperty<A, E>, values: Iterable<E>) {
        argument(property) {
            arrayOf_(values) { enumEntryReference(it) }
        }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> argument(property: ArrayArgumentProperty<A, E>, vararg values: E) {
        argument(property, values.asIterable())
    }

    @JvmName("annotationArgument")
    inline fun <reified Embedded : Annotation> argument(
        property: ArgumentProperty<A, Embedded>,
        annotation: JavaDeferredTypedAnnotation<Embedded>,
    ) {
        argument(property) { L(annotation) }
    }

    @JvmName("annotationArgument")
    inline fun <reified Embedded : Annotation> argument(
        property: ArgumentProperty<A, Embedded>,
        noinline block: JavaAnnotationScope<Embedded>.() -> Unit = {}
    ) {
        argument(property, JavaDeferredTypedAnnotation { of<Embedded>().apply(block).build() })
    }

    @JvmName("annotationArrayArgument")
    inline fun <reified Embedded : Annotation> argument(
        property: ArrayArgumentProperty<A, Embedded>,
        values: Iterable<JavaDeferredTypedAnnotation<Embedded>>
    ) {
        argument(property.name) {
            arrayOf_(values) { L(it) }
        }
    }

    @JvmName("annotationArrayArgument")
    inline fun <reified Embedded : Annotation> argument(
        property: ArrayArgumentProperty<A, Embedded>,
        vararg values: JavaDeferredTypedAnnotation<Embedded>
    ) {
        argument(property, values.asIterable())
    }

    @PublishedApi
    internal fun build(): JPAnnotation =
        specBuilder.build()

    @PublishedApi
    internal companion object {
        fun <A : Annotation> of(className: XClassName): JavaAnnotationScope<A> =
            JavaAnnotationScope(JPAnnotation.builder(className.java))

        fun <A : Annotation> of(kClass: KClass<out A>): JavaAnnotationScope<A> =
            of(XClassName.of(kClass))

        inline fun <reified A : Annotation> of(): JavaAnnotationScope<A> =
            of(A::class)
    }
}
