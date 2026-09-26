package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.parameterizedBy
import io.github.diskria.poetesse.java.JPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPParameterizedTypeName

class XParameterizedTypeName private constructor(
    config: Poetesse.Config,
    val rawType: XClassName,
    val typeArguments: List<XTypeName>,
    override val isNullable: Boolean,
) : XTypedTypeName<KPParameterizedTypeName, JPParameterizedTypeName>(config) {

    init {
        require(typeArguments.isNotEmpty()) { "XParameterizedTypeName requires at least one type argument." }
    }

    override fun interopToKotlinInternal(): KPParameterizedTypeName =
        rawType.interopToKotlin().parameterizedBy(typeArguments.map { it.interopToKotlin() })

    override fun interopToJavaInternal(): JPParameterizedTypeName =
        rawType.interopToJava().parameterizedBy(typeArguments.map { it.box().interopToJava() })

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(rawType: XClassName, typeArguments: List<XTypeName>, isNullable: Boolean = false) =
            XParameterizedTypeName(poetesse.config, rawType, typeArguments, isNullable)
    }
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPParameterizedTypeName.asXParameterizedTypeName() = with(poetesse) {
    XParameterizedTypeName.of(xClass(rawType), typeArguments.map { xType(it) }, isNullable)
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPParameterizedTypeName.asXParameterizedTypeName(nullable: Boolean) = with(poetesse) {
    XParameterizedTypeName.of(xClass(rawType()), typeArguments().map { xType(it) }, nullable)
}

fun XClassName.generic(typeArguments: Iterable<XTypeName>, nullable: Boolean = isNullable) =
    XParameterizedTypeName.of(rawType = this, typeArguments.toList(), nullable)

fun XClassName.generic(vararg typeArguments: XTypeName, nullable: Boolean = isNullable) =
    generic(typeArguments.asIterable(), nullable)
