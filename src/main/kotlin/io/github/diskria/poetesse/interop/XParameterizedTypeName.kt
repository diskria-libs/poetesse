package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.extensions.parameterizedBy
import io.github.diskria.poetesse.java.JPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPParameterizedTypeName

class XParameterizedTypeName internal constructor(
    override val settings: Poetesse.Settings,
    private val rawType: XClassName,
    private val typeArguments: List<XTypeName>,
    override val isNullable: Boolean,
) : XTypedTypeName<KPParameterizedTypeName, JPParameterizedTypeName>() {

    override fun interopToKotlinInternal(): KPParameterizedTypeName =
        rawType.interopToKotlin()
            .parameterizedBy(typeArguments.map { it.interopToKotlin() })

    override fun interopToJavaInternal(): JPParameterizedTypeName =
        rawType.interopToJava(resolveNullability = false)
            .parameterizedBy(typeArguments.map { it.box().interopToJava() })
}

@PublishedApi
context(scope: PoetesseScope)
internal fun KPParameterizedTypeName.asXParameterizedTypeName(): XParameterizedTypeName =
    XParameterizedTypeName(scope.settings, rawType.asX<XClassName>(), typeArguments.map { it.toXType() }, isNullable)

@PublishedApi
context(scope: PoetesseScope)
internal fun JPParameterizedTypeName.asXParameterizedTypeName(nullable: Boolean = false): XParameterizedTypeName =
    XParameterizedTypeName(scope.settings, rawType().asX<XClassName>(), typeArguments().map { it.toXType() }, nullable)

fun XClassName.generic(
    typeArguments: Iterable<XTypeName>,
    nullable: Boolean = isNullable,
): XParameterizedTypeName = XParameterizedTypeName(settings, this, typeArguments.toList(), nullable)

fun XClassName.generic(vararg typeArguments: XTypeName, nullable: Boolean = isNullable): XParameterizedTypeName =
    generic(typeArguments.asIterable(), nullable)
