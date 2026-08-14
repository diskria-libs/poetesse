package io.github.diskria.poetesse

import io.github.diskria.poetesse.java.*
import io.github.diskria.poetesse.kotlin.KotlinFactory
import io.github.diskria.poetesse.kotlin.PoetesseKotlinScope
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class Poetesse private constructor(override val settings: Settings) : PoetesseKotlinScope, PoetesseJavaScope {

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

        fun setNullable(typeName: JPTypeName, isNullable: Boolean): JPTypeName

        companion object {
            val Default: JavaNullabilityResolver = object : JavaNullabilityResolver {

                private val nullableAnnotationType = JPClassName.get(Nullable::class.java)
                private val notNullAnnotationType = JPClassName.get(NotNull::class.java)

                override fun isNullable(typeName: JPTypeName): Boolean =
                    typeName.annotations().any { it.type() == nullableAnnotationType }

                @Suppress("UNCHECKED_CAST")
                override fun setNullable(typeName: JPTypeName, isNullable: Boolean): JPTypeName {
                    val annotationType = if (isNullable) nullableAnnotationType else notNullAnnotationType
                    return typeName.annotated(JPAnnotation.builder(annotationType).build())
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
