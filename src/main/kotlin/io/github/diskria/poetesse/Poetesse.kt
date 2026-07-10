package io.github.diskria.poetesse

import io.github.diskria.poetesse.java.JPRootScope
import io.github.diskria.poetesse.kotlin.KPRootScope

@PoetesseKotlin
@PoetesseJava
class Poetesse private constructor(val settings: Settings) {

    val kotlin: KPRootScope get() = KPRootScope(settings)
    val java: JPRootScope get() = JPRootScope(settings)

    inline operator fun <T> invoke(block: Poetesse.() -> T): T =
        block()

    data class Settings(
        val indent: String = " ".repeat(4),
        val comment: String? = null,
    )

    class Builder {
        var indent: String = Default.settings.indent
        var commentHeader: String? = Default.settings.comment

        fun build(): Poetesse =
            Poetesse(Settings(indent, commentHeader))
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
