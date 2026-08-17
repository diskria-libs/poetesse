package io.github.diskria.poetesse

import io.github.diskria.poetesse.interop.XTypeVariableFactory
import io.github.diskria.poetesse.java.JPAnnotation
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.java.JavaFactory
import io.github.diskria.poetesse.kotlin.KotlinFactory
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class Poetesse private constructor(
    override val config: Config
) : XTypeVariableFactory {

    val kotlin: KotlinFactory get() = KotlinFactory(config)
    val java: JavaFactory get() = JavaFactory(config)

    inline operator fun <T> invoke(block: Poetesse.() -> T): T = block()

    class Config(
        val indent: String = " ".repeat(4),
        val comment: String? = null,
        val skipLangDefaultImports: Boolean = true,
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

                override fun setNullable(typeName: JPTypeName, isNullable: Boolean): JPTypeName {
                    val annotationType = if (isNullable) nullableAnnotationType else notNullAnnotationType
                    return typeName.annotated(JPAnnotation.builder(annotationType).build())
                }
            }
        }
    }

    class Builder {
        var indent: String = Default.config.indent
        var commentHeader: String? = Default.config.comment
        var skipLangDefaultImports: Boolean = Default.config.skipLangDefaultImports
        var javaNullabilityResolver: JavaNullabilityResolver = Default.config.javaNullabilityResolver

        internal fun build() = Poetesse(Config(indent, commentHeader, skipLangDefaultImports, javaNullabilityResolver))

        internal typealias Block = Builder.() -> Unit
    }

    companion object {
        val Default: Poetesse = Poetesse(Config())

        operator fun invoke(block: Builder.Block = {}): Poetesse =
            Builder().apply(block).build()
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
