package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.extensions.capitalized
import io.github.diskria.poetesse.interop.*

sealed interface KotlinTypeVariableContainer : XTypeVariableFactory {

    operator fun XTypeVariableName.unaryPlus(): XTypeVariableName {
        this@KotlinTypeVariableContainer.internal.append(interopToKotlin())
        return this
    }
}

fun KotlinTypeVariableContainer.typeVariable(
    name: String, bounds: Iterable<XTypeName<*, *>> = emptyList(),
    variance: XVariance? = null, reified: Boolean = false,
    nullable: Boolean = false,
) = +XTypeVariableName(settings, name, bounds.toList(), variance, reified, nullable)

fun KotlinTypeVariableContainer.typeVariable(
    name: String, vararg bounds: XTypeName<*, *>,
    variance: XVariance? = null, reified: Boolean = false,
    nullable: Boolean = false,
) = typeVariable(name, bounds.asIterable(), variance, reified, nullable)

fun KotlinTypeVariableContainer.typeVariable(
    bounds: Iterable<XTypeName<*, *>> = emptyList(),
    variance: XVariance? = null, reified: Boolean = false,
    nullable: Boolean = false,
) = EagerDelegate { name -> typeVariable(name.capitalized(), bounds, variance, reified, nullable) }

fun KotlinTypeVariableContainer.typeVariable(
    vararg bounds: XTypeName<*, *>,
    variance: XVariance? = null, reified: Boolean = false,
    nullable: Boolean = false,
) = EagerDelegate { name -> typeVariable(name.capitalized(), bounds.asIterable(), variance, reified, nullable) }

internal interface KotlinTypeVariableContainerInternal {

    fun append(typeVariable: KPTypeVariableName)

    companion object {
        fun of(
            append: (typeVariable: KPTypeVariableName) -> Unit,
        ): KotlinTypeVariableContainerInternal = object : KotlinTypeVariableContainerInternal {
            override fun append(typeVariable: KPTypeVariableName) = append(typeVariable)
        }
    }
}

private val KotlinTypeVariableContainer.internal: KotlinTypeVariableContainerInternal
    get() = when (this) {
        is KotlinTypeScope -> typeVariableContainer
        is KotlinTypeAliasScope -> typeVariableContainer
        is KotlinPropertyScope -> typeVariableContainer
        is KotlinFunctionScope -> typeVariableContainer
    }
