package io.github.diskria.poetesse

import io.github.diskria.poetesse.java.JavaScope
import io.github.diskria.poetesse.kotlin.KotlinScope

@PoetesseKotlin
@PoetesseJava
class PoetesseScope private constructor(val settings: PoetesseSettings) {

    val kotlin: KotlinScope get() = KotlinScope(settings)
    val java: JavaScope get() = JavaScope(settings)

    inline operator fun <T> invoke(block: PoetesseScope.() -> T): T =
        block()

    class Builder {
        var indent: String = Default.settings.indent
        var commentHeader: String? = Default.settings.comment

        fun build(): PoetesseScope =
            PoetesseScope(PoetesseSettings(indent, commentHeader))
    }

    companion object {
        val Default: PoetesseScope = PoetesseScope(PoetesseSettings())

        operator fun invoke(action: Builder.() -> Unit = {}): PoetesseScope =
            Builder().apply(action).build()
    }
}

data class PoetesseSettings(
    val indent: String = " ".repeat(4),
    val comment: String? = null,
)

inline fun <T> poetesse(block: PoetesseScope.() -> T): T =
    PoetesseScope.Default.block()

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class PoetesseKotlin

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class PoetesseJava
