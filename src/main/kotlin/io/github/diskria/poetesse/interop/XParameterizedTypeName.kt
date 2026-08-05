package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.diskria.poetesse.extensions.parameterizedBy
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.java.JPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPParameterizedTypeName

class XParameterizedTypeName internal constructor(
    private val rawType: XClassName,
    private val typeArguments: List<XTypeName>,
    override val nullable: Boolean = false,
) : XTypeName() {

    override val javaAsKotlin: KPParameterizedTypeName
        get() = rawType.javaAsKotlin
            .parameterizedBy(typeArguments.map { it.javaAsKotlin })
            .setNullable(nullable)

    override val kotlinAsJava: JPParameterizedTypeName
        get() = rawType.kotlinAsJava
            .parameterizedBy(typeArguments.map { it.ensureBoxed().kotlinAsJava })

    override fun interopToKotlin(): KPParameterizedTypeName =
        rawType.interopToKotlin()
            .parameterizedBy(typeArguments.map { it.interopToKotlin() })
            .setNullable(nullable)

    override fun interopToJava(): JPParameterizedTypeName =
        rawType.interopToJava()
            .parameterizedBy(typeArguments.map { it.ensureBoxed().interopToJava() })

    override fun setNullableInternal(nullable: Boolean): XParameterizedTypeName =
        XParameterizedTypeName(rawType, typeArguments, nullable)
}

fun XClassName.parameterizedBy(typeArguments: Iterable<XTypeName>): XParameterizedTypeName =
    XParameterizedTypeName(this, typeArguments.toList())

fun XClassName.parameterizedBy(vararg typeArguments: XTypeName): XParameterizedTypeName =
    parameterizedBy(typeArguments.asIterable())

fun KPParameterizedTypeName.asXParameterizedTypeName(): XParameterizedTypeName =
    XParameterizedTypeName(rawType.asXClassName(), typeArguments.map { it.asXTypeName() }, isNullable)

fun JPParameterizedTypeName.asXParameterizedTypeName(): XParameterizedTypeName =
    XParameterizedTypeName(rawType().asXClassName(), typeArguments().map { it.asXTypeName() }, false)
