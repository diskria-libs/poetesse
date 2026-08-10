package io.github.diskria.poetesse

import io.github.diskria.poetesse.java.JPAnnotation
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.java.JavaFactory
import io.github.diskria.poetesse.kotlin.KotlinFactory
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@PoetesseKotlin
@PoetesseJava
class Poetesse private constructor(override val settings: Settings) : PoetesseScope {

    val kotlin: KotlinFactory get() = KotlinFactory(settings)
    val java: JavaFactory get() = JavaFactory(settings)

    inline operator fun <T> invoke(block: Poetesse.() -> T): T =
        block()

    class Settings(
        val indent: String = " ".repeat(4),
        val comment: String? = null,
        val javaNullabilityResolver: JavaNullabilityResolver = JavaNullabilityResolver.Default,
    )

    interface JavaNullabilityResolver {

        fun isNullable(typeName: JPTypeName): Boolean

        fun <J : JPTypeName> setNullable(typeName: JPTypeName, isNullable: Boolean): J

        companion object {
            val Default: JavaNullabilityResolver = object : JavaNullabilityResolver {

                private val nullableAnnotationType = JPClassName.get(Nullable::class.java)
                private val notNullAnnotationType = JPClassName.get(NotNull::class.java)

                override fun isNullable(typeName: JPTypeName): Boolean =
                    typeName.annotations().any { it.type() == nullableAnnotationType }

                @Suppress("UNCHECKED_CAST")
                override fun <J : JPTypeName> setNullable(typeName: JPTypeName, isNullable: Boolean): J {
                    val annotationType = if (isNullable) nullableAnnotationType else notNullAnnotationType
                    return typeName.annotated(JPAnnotation.builder(annotationType).build()) as J
                }
            }
        }
    }

    class Builder {
        var indent: String = Default.settings.indent
        var commentHeader: String? = Default.settings.comment
        var javaNullabilityResolver: JavaNullabilityResolver = Default.settings.javaNullabilityResolver

        internal fun build(): Poetesse =
            Poetesse(Settings(indent, commentHeader, javaNullabilityResolver))
    }

    companion object {
        val Default: Poetesse = Poetesse(Settings())

        operator fun invoke(action: Builder.() -> Unit = {}): Poetesse =
            Builder().apply(action).build()
    }
}

inline fun <T> poetesse(block: Poetesse.() -> T): T =
    Poetesse.Default.block()

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class PoetesseKotlin

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class PoetesseJava

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class PoetesseX
