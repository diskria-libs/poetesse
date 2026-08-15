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

    fun argument(name: String, value: JavaCodeRef) {
        member(name, value.codeBlock)
    }

    fun argument(name: String, block: JavaCodeScope.Block) {
        argument(name, JavaCodeScope.of(block))
    }

    @JvmName("stringArgument")
    fun argument(property: ArgumentProperty<A, String>, value: String) {
        argument(property.name) { S(value) }
    }

    @JvmName("stringArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, String>, values: Iterable<String>) {
        argument(property.name) {
            expression.arrayOf(values) { S(it) }
        }
    }

    @JvmName("stringArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, String>, vararg values: String) {
        argument(property, values.asIterable())
    }

    @JvmName("booleanArgument")
    fun argument(property: ArgumentProperty<A, Boolean>, value: Boolean) {
        argument(property.name) { L(value) }
    }

    @JvmName("booleanArrayArgument")
    fun argument(property: ArgumentProperty<A, BooleanArray>, values: Iterable<Boolean>) {
        argument(property.name) {
            expression.arrayOf(values) { L(it) }
        }
    }

    @JvmName("booleanArrayArgument")
    fun argument(property: ArgumentProperty<A, BooleanArray>, vararg values: Boolean) {
        argument(property, values.asIterable())
    }

    @JvmName("intArgument")
    fun argument(property: ArgumentProperty<A, Int>, value: Int) {
        argument(property.name) { L(value) }
    }

    @JvmName("intArrayArgument")
    fun argument(property: ArgumentProperty<A, IntArray>, values: Iterable<Int>) {
        argument(property.name) {
            expression.arrayOf(values) { L(it) }
        }
    }

    @JvmName("intArrayArgument")
    fun argument(property: ArgumentProperty<A, IntArray>, vararg values: Int) {
        argument(property, values.asIterable())
    }

    @JvmName("classArgument")
    fun argument(property: ArgumentProperty<A, KClass<*>>, value: KClass<*>) {
        argument(property.name) { expression.classLiteral(value) }
    }

    @JvmName("classArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, values: Iterable<KClass<*>>) {
        argument(property.name) {
            expression.arrayOf(values) { expression.classLiteral(it) }
        }
    }

    @JvmName("classArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, vararg values: KClass<*>) {
        argument(property, values.asIterable())
    }

    @JvmName("xTypeArgument")
    fun argument(property: ArgumentProperty<A, KClass<*>>, value: XTypeName) {
        argument(property.name) { expression.classLiteral(value) }
    }

    @JvmName("xTypeArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, values: Iterable<XTypeName>) {
        argument(property.name) {
            expression.arrayOf(values) { expression.classLiteral(it) }
        }
    }

    @JvmName("classNameArrayArgument")
    fun argument(property: ArrayArgumentProperty<A, KClass<*>>, vararg values: XTypeName) {
        argument(property, values.asIterable())
    }

    @JvmName("enumArgument")
    inline fun <reified E : Enum<E>> argument(property: ArgumentProperty<A, E>, value: E) {
        argument(property.name) { expression.enumEntry(value) }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> argument(property: ArrayArgumentProperty<A, E>, values: Iterable<E>) {
        argument(property.name) {
            expression.arrayOf(values) { expression.enumEntry(it) }
        }
    }

    @JvmName("enumArrayArgument")
    inline fun <reified E : Enum<E>> argument(property: ArrayArgumentProperty<A, E>, vararg values: E) {
        argument(property, values.asIterable())
    }

    @JvmName("annotationArgument")
    inline fun <reified Nested : Annotation> argument(
        property: ArgumentProperty<A, Nested>,
        annotation: JavaTypedAnnotationRef<Nested>,
    ) {
        argument(property.name) { L(annotation) }
    }

    @JvmName("annotationArgument")
    inline fun <reified Nested : Annotation> argument(
        property: ArgumentProperty<A, Nested>,
        noinline block: Block<Nested> = {}
    ) {
        argument(property, JavaTypedAnnotationRef {
            of<Nested>(xClass<Nested>()).apply(block).build()
        })
    }

    @JvmName("annotationArrayArgument")
    inline fun <reified Nested : Annotation> argument(
        property: ArrayArgumentProperty<A, Nested>,
        values: Iterable<JavaTypedAnnotationRef<Nested>>
    ) {
        argument(property.name) {
            expression.arrayOf(values) { L(it) }
        }
    }

    @JvmName("annotationArrayArgument")
    inline fun <reified Nested : Annotation> argument(
        property: ArrayArgumentProperty<A, Nested>,
        vararg values: JavaTypedAnnotationRef<Nested>
    ) {
        argument(property, values.asIterable())
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
