package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.ContextParameter
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToKotlin

class KotlinContextParameterScope private constructor(
    override val config: Poetesse.Config,
    private val name: String,
    private val type: XTypeName,
) : PoetesseKotlinScope {

    internal typealias Block = KotlinContextParameterScope.() -> Unit

    internal fun build() = ContextParameter(name, type.interopToKotlin())

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String, type: XTypeName) =
            KotlinContextParameterScope(poetesse.config, name, type)
    }
}
