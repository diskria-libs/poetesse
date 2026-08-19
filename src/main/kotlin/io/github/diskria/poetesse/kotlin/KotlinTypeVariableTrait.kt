package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.extensions.capitalized
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.XTypeVariableName
import io.github.diskria.poetesse.interop.XVariance
import io.github.diskria.poetesse.interop.interopToKotlin

sealed interface KotlinTypeVariableTrait : PoetesseKotlinScope {
    operator fun XTypeVariableName.unaryPlus(): XTypeVariableName {
        this@KotlinTypeVariableTrait.container.append(interopToKotlin())
        return this
    }
}

fun KotlinTypeVariableTrait.typeVariable(
    name: String,
    bounds: Iterable<XTypeName> = emptyList(),
    variance: XVariance? = null,
    reified: Boolean = false,
    nullable: Boolean = false,
) = +XTypeVariableName.of(name, bounds.toList(), variance, reified, nullable)

fun KotlinTypeVariableTrait.typeVariable(
    name: String,
    vararg bounds: XTypeName,
    variance: XVariance? = null,
    reified: Boolean = false,
    nullable: Boolean = false,
) = typeVariable(name, bounds.asIterable(), variance, reified, nullable)

fun KotlinTypeVariableTrait.typeVariable(
    bounds: Iterable<XTypeName> = emptyList(),
    variance: XVariance? = null,
    reified: Boolean = false,
    nullable: Boolean = false,
) = EagerDelegate { typeVariable(it.capitalized(), bounds, variance, reified, nullable) }

fun KotlinTypeVariableTrait.typeVariable(
    vararg bounds: XTypeName, variance: XVariance? = null, reified: Boolean = false, nullable: Boolean = false,
) = EagerDelegate { typeVariable(it.capitalized(), bounds.asIterable(), variance, reified, nullable) }

internal class KotlinTypeVariableContainer(val append: (typeVariable: KPTypeVariableName) -> Unit)

private val KotlinTypeVariableTrait.container: KotlinTypeVariableContainer
    get() = when (this) {
        is KotlinTypeScope -> typeVariableContainer
        is KotlinTypeAliasScope -> typeVariableContainer
        is KotlinPropertyScope -> typeVariableContainer
        is KotlinFunctionScope -> typeVariableContainer
    }
