package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.qualifiedName
import io.github.diskria.poetesse.java.JPParameterizedTypeName
import io.github.diskria.poetesse.kotlin.KPFunctionalTypeName

class XFunctionalTypeName private constructor(
    config: Poetesse.Config,
    val contextParameters: List<XTypeName>,
    val receiver: XTypeName?,
    val parameters: List<XParameter>,
    val returnType: XTypeName,
    override val isNullable: Boolean,
) : XTypedTypeName<KPFunctionalTypeName, JPParameterizedTypeName>(config) {

    val arity: Int = contextParameters.size + (if (receiver != null) 1 else 0) + parameters.size
    val hasBigArity: Boolean = arity !in FIXED_FUNCTION_ARITY_RANGE
    val jvmRawClassName: XClassName
        get() = if (hasBigArity) {
            xClass(JVM_FUNCTION_N, nullable = isNullable)
        } else {
            xClass("$JVM_FUNCTION_FQCN_PREFIX$arity", nullable = isNullable)
        }

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
        return XParameterizedTypeName.of(jvmRawClassName, typeArguments).interopToJava()
    }

    companion object {
        const val JVM_FUNCTION_N = "$JVM_FUNCTION_FQCN_PREFIX$JVM_FUNCTION_N_SUFFIX"

        context(poetesse: PoetesseScope)
        internal fun of(
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
internal fun KPFunctionalTypeName.asXFunctionalTypeName() = XFunctionalTypeName.of(
    contextParameters = contextParameters.map { poetesse.xType(it) },
    receiver = receiver?.let { poetesse.xType(it) },
    parameters = parameters.map { it.asXParameter() },
    returnType = poetesse.xType(returnType),
    isNullable = isNullable,
)

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPParameterizedTypeName.asXFunctionalTypeNameOrNull(nullable: Boolean): XFunctionalTypeName? {
    val typeArguments = typeArguments()
    val arity = (typeArguments.size - 1).takeIf { it >= 0 } ?: return null
    val qualifiedName = rawType().qualifiedName
    if (!qualifiedName.startsWith(JVM_FUNCTION_FQCN_PREFIX)) return null
    val suffix = qualifiedName.removePrefix(JVM_FUNCTION_FQCN_PREFIX)
    if (suffix != JVM_FUNCTION_N_SUFFIX) {
        val expectedArity = suffix.toIntOrNull()?.takeIf { it in FIXED_FUNCTION_ARITY_RANGE } ?: return null
        if (arity != expectedArity) return null
    }
    return XFunctionalTypeName.of(
        contextParameters = emptyList(),
        receiver = null,
        parameters = typeArguments.take(arity).map { XParameter(type = poetesse.xType(it)) },
        returnType = poetesse.xType(typeArguments.last()),
        isNullable = nullable,
    )
}

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

private const val JVM_FUNCTION_FQCN_PREFIX = "kotlin.jvm.functions.Function"
private const val JVM_FUNCTION_N_SUFFIX = "N"
private val FIXED_FUNCTION_ARITY_RANGE = 0..22
