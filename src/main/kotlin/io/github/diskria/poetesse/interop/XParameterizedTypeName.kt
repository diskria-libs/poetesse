package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.parameterizedBy
import io.github.diskria.poetesse.java.JPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPParameterizedTypeName

class XParameterizedTypeName private constructor(
    config: Poetesse.Config,
    private val rawType: XClassName,
    private val typeArguments: List<XTypeName>,
    override val isNullable: Boolean,
) : XTypedTypeName<KPParameterizedTypeName, JPParameterizedTypeName>(config) {

    override fun interopToKotlinInternal(): KPParameterizedTypeName =
        rawType.interopToKotlin()
            .parameterizedBy(typeArguments.map { it.interopToKotlin() })

    override fun interopToJavaInternal(): JPParameterizedTypeName =
        rawType.interopToJava(resolveNullability = false)
            .parameterizedBy(typeArguments.map { it.box().interopToJava() })

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(rawType: XClassName, typeArguments: List<XTypeName>, isNullable: Boolean) =
            XParameterizedTypeName(poetesse.config, rawType, typeArguments, isNullable)
    }
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPParameterizedTypeName.asXParameterizedTypeName() = with(poetesse) {
    XParameterizedTypeName.of(rawType.asX<XClassName>(), typeArguments.map { xType(it) }, isNullable)
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPParameterizedTypeName.asXParameterizedTypeName(nullable: Boolean = false) = with(poetesse) {
    XParameterizedTypeName.of(rawType().asX<XClassName>(), typeArguments().map { xType(it) }, nullable)
}

fun XClassName.generic(typeArguments: Iterable<XTypeName>, nullable: Boolean = isNullable) =
    XParameterizedTypeName.of(rawType = this, typeArguments.toList(), nullable)

fun XClassName.generic(vararg typeArguments: XTypeName, nullable: Boolean = isNullable) =
    generic(typeArguments.asIterable(), nullable)
