package io.github.diskria.poetesse.interop

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.qualifiedName
import io.github.diskria.poetesse.java.JPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPFunctionalTypeName
import io.github.diskria.poetesse.utils.StringAffix

class XFunctionalTypeName private constructor(
    config: Poetesse.Config,
    val contextParameters: List<XTypeName>,
    val receiver: XTypeName?,
    val parameters: List<XParameter>,
    val returnType: XTypeName,
    override val isNullable: Boolean,
) : XTypedTypeName<KPFunctionalTypeName, JPParameterizedTypeName>(config) {

    @OptIn(ExperimentalKotlinPoetApi::class)
    override fun interopToKotlinInternal(): KPFunctionalTypeName =
        KPFunctionalTypeName.get(
            contextParameters = contextParameters.map { it.interopToKotlin() },
            receiver = receiver?.interopToKotlin(),
            parameters = parameters.map { it.interopToKotlin(fallbackName = "") },
            returnType = returnType.interopToKotlin(),
        )

    override fun interopToJavaInternal(): JPParameterizedTypeName {
        val typeArguments = buildList {
            contextParameters.forEach { add(it) }
            receiver?.let { add(it) }
            parameters.forEach { add(it.type) }
        }
        val arity = typeArguments.size
        if (arity > 22) {
            error("Function arity $arity exceeds max supported JVM function arity of 22")
        }
        val functionClassName = xClass(jvmFunctionAffix.wrap(arity.toString()))
        return XParameterizedTypeName.of(functionClassName, typeArguments + returnType).interopToJava()
    }

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(
            contextParameters: List<XTypeName>,
            receiver: XTypeName?,
            parameters: List<XParameter>,
            returnType: XTypeName,
            isNullable: Boolean = false,
        ) = XFunctionalTypeName(poetesse.config, contextParameters, receiver, parameters, returnType, isNullable)
    }
}

private val jvmFunctionAffix = StringAffix(prefix = "kotlin.jvm.functions.Function")

@OptIn(ExperimentalKotlinPoetApi::class)
@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPFunctionalTypeName.asXFunctionalTypeName() = with(poetesse) {
    XFunctionalTypeName.of(
        contextParameters = contextParameters.map { xType(it) },
        receiver = receiver?.let { xType(it) },
        parameters = parameters.map { it.asXParameter() },
        returnType = xType(returnType),
        isNullable = isNullable,
    )
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPParameterizedTypeName.asXFunctionalTypeNameOrNull(nullable: Boolean = false): XFunctionalTypeName? {
    val typeArguments = typeArguments()
    val arity = jvmFunctionAffix.unwrapOrNull(rawType().qualifiedName)?.toIntOrNull() ?: return null
    if (typeArguments.size != arity + 1) {
        return null
    }
    val parameterTypes = typeArguments.take(arity)
    val returnType = typeArguments.last()
    return with(poetesse) {
        XFunctionalTypeName.of(
            contextParameters = emptyList(),
            receiver = null,
            parameters = parameterTypes.map { XParameter(type = xType(it)) },
            returnType = xType(returnType),
            isNullable = nullable,
        )
    }
}

fun XTypeName.lambda(
    parameters: Iterable<XParameter> = emptyList(), receiver: XTypeName? = null,
    contextParameters: Iterable<XTypeName> = emptyList(), nullable: Boolean = false,
) = XFunctionalTypeName.of(
    contextParameters = contextParameters.toList(),
    receiver = receiver,
    parameters = parameters.toList(),
    returnType = this,
    isNullable = nullable,
)

@JvmName("lambdaWithParameterTypes")
fun XTypeName.lambda(
    parameters: Iterable<XTypeName> = emptyList(), receiver: XTypeName? = null,
    contextParameters: Iterable<XTypeName> = emptyList(), nullable: Boolean = false,
) = lambda(parameters.map { XParameter(type = it) }, receiver, contextParameters, nullable)

fun XTypeName.lambda(
    receiver: XTypeName? = null, contextParameters: Iterable<XTypeName> = emptyList(), nullable: Boolean = false,
) = lambda(emptyList<XTypeName>(), receiver, contextParameters, nullable)

fun XTypeName.lambda(vararg parameters: XTypeName, nullable: Boolean = false) =
    lambda(parameters = parameters.asIterable(), nullable = nullable)
