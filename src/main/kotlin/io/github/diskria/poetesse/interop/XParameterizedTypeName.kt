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
        context(scope: PoetesseXScope)
        fun of(rawType: XClassName, typeArguments: List<XTypeName>, isNullable: Boolean) =
            XParameterizedTypeName(scope.config, rawType, typeArguments, isNullable)
    }
}

@PublishedApi
context(scope: PoetesseXScope)
internal fun KPParameterizedTypeName.asXParameterizedTypeName() =
    XParameterizedTypeName.of(rawType.asX<XClassName>(), typeArguments.map { it.toXType() }, isNullable)

@PublishedApi
context(scope: PoetesseXScope)
internal fun JPParameterizedTypeName.asXParameterizedTypeName(nullable: Boolean = false) = with(scope) {
    XParameterizedTypeName.of(rawType().asX<XClassName>(), typeArguments().map { xType(it) }, nullable)
}

fun XClassName.generic(typeArguments: Iterable<XTypeName>, nullable: Boolean = isNullable) =
    XParameterizedTypeName.of(rawType = this, typeArguments.toList(), nullable)

fun XClassName.generic(vararg typeArguments: XTypeName, nullable: Boolean = isNullable) =
    generic(typeArguments.asIterable(), nullable)
