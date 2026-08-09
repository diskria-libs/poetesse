package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.diskria.poetesse.extensions.parameterizedBy
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.java.JPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPParameterizedTypeName

class XParameterizedTypeName internal constructor(
    private val rawType: XClassName,
    private val typeArguments: List<XTypeName>,
    override val isNullable: Boolean = false,
) : XTypeName() {

    override fun interopToKotlin(): KPParameterizedTypeName =
        rawType.interopToKotlin().parameterizedBy(typeArguments.map { it.interopToKotlin() }).setNullable(isNullable)

    override fun interopToJava(): JPParameterizedTypeName =
        rawType.interopToJava().parameterizedBy(typeArguments.map { it.ensureBoxed().interopToJava() })

    override fun setNullable(nullable: Boolean): XParameterizedTypeName =
        XParameterizedTypeName(rawType, typeArguments, nullable)
}

fun XClassName.generic(typeArguments: Iterable<XTypeName>): XParameterizedTypeName =
    XParameterizedTypeName(this, typeArguments.toList())

fun XClassName.generic(vararg typeArguments: XTypeName): XParameterizedTypeName =
    generic(typeArguments.asIterable())

fun KPParameterizedTypeName.asXParameterizedTypeName(): XParameterizedTypeName =
    XParameterizedTypeName(rawType.asXClassName(), typeArguments.map { it.asXTypeName() }, isNullable)

fun JPParameterizedTypeName.asXParameterizedTypeName(): XParameterizedTypeName =
    XParameterizedTypeName(rawType().asXClassName(), typeArguments().map { it.asXTypeName() }, false)
