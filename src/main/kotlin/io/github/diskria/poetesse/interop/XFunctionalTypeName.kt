package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.java.JPClassName
import io.github.diskria.poetesse.java.JPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPLambdaTypeName

class XFunctionalTypeName(
    val receiver: XTypeName? = null,
    val parameters: List<XNamedType> = emptyList(),
    val returnType: XTypeName,
    override val nullable: Boolean = false,
) : XTypeName() {

    override fun interopToKotlin(): KPLambdaTypeName =
        KPLambdaTypeName.get(
            receiver = receiver?.interopToKotlin(),
            parameters = parameters.map { it.interopToKotlinFunctionalTypeParameter() },
            returnType = returnType.interopToKotlin(),
        ).setNullable(nullable)

    override fun interopToJava(): JPParameterizedTypeName {
        val allArguments = buildList {
            if (receiver != null) {
                add(receiver.interopToJava())
            }
            parameters.forEach { add(it.type.interopToJava()) }
        }

        val arity = allArguments.size
        if (arity > 22) {
            error("Function arity $arity exceeds max supported JVM function arity of 22")
        }

        val functionClass = JPClassName.get("kotlin.jvm.functions", "Function$arity")
        val typeArguments = (allArguments + returnType.interopToJava()).toTypedArray()
        return JPParameterizedTypeName.get(functionClass, *typeArguments)
    }

    override fun setNullableInternal(nullable: Boolean): XTypeName =
        XFunctionalTypeName(receiver, parameters, returnType, nullable)
}

fun KPLambdaTypeName.asXFunctionalTypeName(): XFunctionalTypeName =
    XFunctionalTypeName(
        receiver = receiver?.asXTypeName(),
        parameters = parameters.map { it.asXNamedType() },
        returnType = returnType.asXTypeName(),
        nullable = isNullable,
    )

fun JPParameterizedTypeName.asXFunctionalTypeNameOrNull(): XFunctionalTypeName? {
    val rawType = rawType()
    if (rawType.packageName() != "kotlin.jvm.functions" || !rawType.simpleName().startsWith("Function")) {
        return null
    }
    val arity = rawType.simpleName().removePrefix("Function").toIntOrNull() ?: return null
    val typeArguments = typeArguments()
    if (typeArguments.size != arity + 1) {
        return null
    }
    val parameterTypes = typeArguments.dropLast(1)
    val returnType = typeArguments.last()
    return XFunctionalTypeName(
        receiver = null,
        parameters = parameterTypes.map { XNamedType(type = it.asXTypeName()) },
        returnType = returnType.asXTypeName(),
    )
}
