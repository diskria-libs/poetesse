package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.isCoroutinesContinuationType
import io.github.diskria.poetesse.extensions.qualifiedName
import io.github.diskria.poetesse.java.JPParameterizedTypeName
import io.github.diskria.poetesse.java.JPWildcardTypeName
import io.github.diskria.poetesse.kotlin.KPFunctionalTypeName

class XFunctionalTypeName private constructor(
    config: Poetesse.Config,
    val contextParameters: List<XTypeName>,
    val receiver: XTypeName?,
    val parameters: List<XParameter>,
    val returnType: XTypeName,
    val isSuspending: Boolean,
    override val isNullable: Boolean,
) : XTypedTypeName<KPFunctionalTypeName, JPParameterizedTypeName>(config) {

    val arity: Int = contextParameters.size + (if (receiver != null) 1 else 0) + parameters.size
    val jvmArity: Int = arity + if (isSuspending) 1 else 0
    val isBigArity: Boolean = jvmArity !in FIXED_FUNCTION_ARITY_RANGE
    val jvmRawClassName: XClassName by lazy {
        if (isBigArity) {
            xClass(JVM_FUNCTION_N, nullable = isNullable)
        } else {
            xClass("$JVM_FUNCTION_FQCN_PREFIX$jvmArity", nullable = isNullable)
        }
    }
    val jvmParameterizedTypeName by lazy {
        val typeArguments = buildList {
            addAll(contextParameters)
            receiver?.let { add(it) }
            addAll(parameters.map { it.type })
            if (isSuspending) {
                add(xClass(COROUTINES_CONTINUATION_FQCN).generic(returnType.consumer()))
                add(xType<Any>())
            } else {
                add(returnType)
            }
        }
        XParameterizedTypeName.of(jvmRawClassName, typeArguments)
    }

    override fun interopToKotlinInternal(): KPFunctionalTypeName =
        KPFunctionalTypeName.get(
            contextParameters = contextParameters.map { it.interopToKotlin() },
            receiver = receiver?.interopToKotlin(),
            parameters = parameters.map { it.interopToKotlin(fallbackName = "") },
            returnType = returnType.interopToKotlin(),
        ).copy(suspending = isSuspending)

    override fun interopToJavaInternal(): JPParameterizedTypeName = jvmParameterizedTypeName.interopToJava()

    companion object {
        const val JVM_FUNCTION_N = "$JVM_FUNCTION_FQCN_PREFIX$JVM_FUNCTION_N_SUFFIX"
        const val JVM_MAX_ARITY = 22
        const val COROUTINES_CONTINUATION_FQCN = "kotlin.coroutines.Continuation"

        context(poetesse: PoetesseScope)
        internal fun of(
            contextParameters: List<XTypeName> = emptyList(),
            receiver: XTypeName? = null,
            parameters: List<XParameter> = emptyList(),
            returnType: XTypeName,
            isSuspending: Boolean = false,
            isNullable: Boolean = false,
        ) = XFunctionalTypeName(
            poetesse.config,
            contextParameters,
            receiver,
            parameters,
            returnType,
            isSuspending,
            isNullable,
        )
    }
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPFunctionalTypeName.asXFunctionalTypeName() = XFunctionalTypeName.of(
    contextParameters = contextParameters.map { poetesse.xType(it) },
    receiver = receiver?.let { poetesse.xType(it) },
    parameters = parameters.map { it.asXParameter() },
    returnType = poetesse.xType(returnType),
    isSuspending = isSuspending,
    isNullable = isNullable,
)

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPParameterizedTypeName.asXFunctionalTypeNameOrNull(nullable: Boolean): XFunctionalTypeName? {
    val typeArguments = typeArguments()
    val qualifiedName = rawType().qualifiedName
    if (!qualifiedName.startsWith(JVM_FUNCTION_FQCN_PREFIX)) return null
    val suffix = qualifiedName.removePrefix(JVM_FUNCTION_FQCN_PREFIX)
    if (suffix == JVM_FUNCTION_N_SUFFIX) {
        val returnType = typeArguments.singleOrNull() ?: return null
        return XFunctionalTypeName.of(
            returnType = poetesse.xType(returnType),
            isNullable = nullable,
        )
    }
    val expectedArity = suffix.toIntOrNull()?.takeIf { it in FIXED_FUNCTION_ARITY_RANGE } ?: return null
    if (typeArguments.size - 1 != expectedArity) return null
    val parameterTypes = typeArguments.toMutableList()
    val returnType = parameterTypes.removeLastOrNull() ?: return null
    val lastParameterType = parameterTypes.lastOrNull()
    val (actualReturnType, isSuspending) = when {
        lastParameterType is JPParameterizedTypeName && lastParameterType.isCoroutinesContinuationType(returnType) -> {
            val wildcard = lastParameterType.typeArguments().singleOrNull() as? JPWildcardTypeName ?: return null
            val boundary = wildcard.lowerBounds().singleOrNull() ?: return null
            parameterTypes.removeLastOrNull() ?: return null
            boundary to true
        }

        else -> returnType to false
    }
    return XFunctionalTypeName.of(
        parameters = parameterTypes.map { XParameter(type = poetesse.xType(it)) },
        returnType = poetesse.xType(actualReturnType),
        isSuspending = isSuspending,
        isNullable = nullable,
    )
}

fun XTypeName.lambda(
    parameters: Iterable<XParameter> = emptyList(),
    receiver: XTypeName? = null,
    contextParameters: Iterable<XTypeName> = emptyList(),
    isSuspending: Boolean = false,
    nullable: Boolean = false,
): XFunctionalTypeName = XFunctionalTypeName.of(
    contextParameters = contextParameters.toList(),
    receiver = receiver,
    parameters = parameters.toList(),
    returnType = this,
    isSuspending = isSuspending,
    isNullable = nullable,
)

@JvmName("lambdaWithParameterTypes")
fun XTypeName.lambda(
    parameters: Iterable<XTypeName> = emptyList(),
    receiver: XTypeName? = null,
    contextParameters: Iterable<XTypeName> = emptyList(),
    isSuspending: Boolean = false,
    nullable: Boolean = false,
): XFunctionalTypeName = lambda(
    parameters = parameters.map { XParameter(type = it) },
    receiver = receiver,
    contextParameters = contextParameters,
    isSuspending = isSuspending,
    nullable = nullable,
)

fun XTypeName.lambda(
    receiver: XTypeName? = null,
    contextParameters: Iterable<XTypeName> = emptyList(),
    isSuspending: Boolean,
    nullable: Boolean = false,
): XFunctionalTypeName = lambda(
    parameters = emptyList<XTypeName>(),
    receiver = receiver,
    contextParameters = contextParameters,
    isSuspending = isSuspending,
    nullable = nullable,
)

fun XTypeName.lambda(
    vararg parameters: XTypeName,
    isSuspending: Boolean = false,
    nullable: Boolean = false
): XFunctionalTypeName = lambda(
    parameters = parameters.asIterable(),
    isSuspending = isSuspending,
    nullable = nullable,
)

private const val JVM_FUNCTION_FQCN_PREFIX = "kotlin.jvm.functions.Function"
private const val JVM_FUNCTION_N_SUFFIX = "N"
private val FIXED_FUNCTION_ARITY_RANGE = 0..XFunctionalTypeName.JVM_MAX_ARITY
