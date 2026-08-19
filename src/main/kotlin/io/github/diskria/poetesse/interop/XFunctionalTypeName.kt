package io.github.diskria.poetesse.interop

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

    override fun interopToKotlinInternal(): KPFunctionalTypeName =
        KPFunctionalTypeName.get(
            contextParameters = contextParameters.map { it.interopToKotlin() },
            receiver = receiver?.interopToKotlin(),
            parameters = parameters.map { it.interopToKotlin(fallbackName = "") },
            returnType = returnType.interopToKotlin(),
        )

    override fun interopToJavaInternal(): JPParameterizedTypeName {
        val typeArguments = buildList {
            addAll(contextParameters)
            receiver?.let { add(it) }
            addAll(parameters.map { it.type })
            add(returnType)
        }
        val arity = countArity(typeArguments)
        val jvmFunctionClassName = xClass(jvmFunctionAffix.wrap(arity.toString()))
        return XParameterizedTypeName.of(jvmFunctionClassName, typeArguments).interopToJava()
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
internal fun JPParameterizedTypeName.asXFunctionalTypeNameOrNull(nullable: Boolean): XFunctionalTypeName? {
    val typeArguments = typeArguments()
    val arity = countArityOrNull(typeArguments) ?: return null
    val jvmFunctionArity = jvmFunctionAffix.unwrapOrNull(rawType().qualifiedName)?.toIntOrNull()
    if (arity != jvmFunctionArity) return null
    return with(poetesse) {
        XFunctionalTypeName.of(
            contextParameters = emptyList(),
            receiver = null,
            parameters = typeArguments.take(arity).map { XParameter(type = xType(it)) },
            returnType = xType(typeArguments.last()),
            isNullable = nullable,
        )
    }
}

private val jvmFunctionAffix = StringAffix(prefix = "kotlin.jvm.functions.Function")

private fun countArityOrNull(typeArguments: List<*>): Int? = (typeArguments.size - 1).takeIf { it in 0..22 }

private fun countArity(typeArguments: List<*>): Int =
    requireNotNull(countArityOrNull(typeArguments)) { "JVM function arity ${typeArguments.size} is not valid" }

fun XTypeName.lambda(
    parameters: Iterable<XParameter> = emptyList(),
    receiver: XTypeName? = null,
    contextParameters: Iterable<XTypeName> = emptyList(),
    nullable: Boolean = false,
) = XFunctionalTypeName.of(
    contextParameters = contextParameters.toList(),
    receiver = receiver,
    parameters = parameters.toList(),
    returnType = this,
    isNullable = nullable,
)

@JvmName("lambdaWithParameterTypes")
fun XTypeName.lambda(
    parameters: Iterable<XTypeName> = emptyList(),
    receiver: XTypeName? = null,
    contextParameters: Iterable<XTypeName> = emptyList(),
    nullable: Boolean = false,
) = lambda(parameters.map { XParameter(type = it) }, receiver, contextParameters, nullable)

fun XTypeName.lambda(
    receiver: XTypeName? = null, contextParameters: Iterable<XTypeName> = emptyList(), nullable: Boolean = false,
) = lambda(emptyList<XTypeName>(), receiver, contextParameters, nullable)

fun XTypeName.lambda(vararg parameters: XTypeName, nullable: Boolean = false) =
    lambda(parameters = parameters.asIterable(), nullable = nullable)
