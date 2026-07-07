package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.FunSpec
import io.github.diskria.poetesse.PoetesseKotlin

@PoetesseKotlin
class KPFunctionScope private constructor(
    private val specBuilder: FunSpec.Builder
) {

    internal fun build(): FunSpec =
        specBuilder.build()

    internal companion object {
        fun of(name: String): KPFunctionScope =
            KPFunctionScope(FunSpec.builder(name))
    }
}
