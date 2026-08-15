package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.extensions.capitalized
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.XTypeVariableName
import io.github.diskria.poetesse.interop.interopToJava

sealed interface JavaTypeVariableContainer : PoetesseJavaScope {
    operator fun XTypeVariableName.unaryPlus(): XTypeVariableName {
        this@JavaTypeVariableContainer.internal.append(interopToJava())
        return this
    }
}

fun JavaTypeVariableContainer.typeVariable(
    name: String, bounds: Iterable<XTypeName> = emptyList(), nullable: Boolean = false
) = +XTypeVariableName.of(name, bounds.toList(), null, false, nullable)

fun JavaTypeVariableContainer.typeVariable(name: String, vararg bounds: XTypeName, nullable: Boolean = false) =
    typeVariable(name, bounds.asIterable(), nullable)

fun JavaTypeVariableContainer.typeVariable(bounds: Iterable<XTypeName> = emptyList(), nullable: Boolean = false) =
    EagerDelegate { name -> typeVariable(name.capitalized(), bounds, nullable) }

fun JavaTypeVariableContainer.typeVariable(vararg bounds: XTypeName, nullable: Boolean = false) =
    EagerDelegate { name -> typeVariable(name.capitalized(), bounds.asIterable(), nullable) }

internal interface JavaTypeVariableContainerInternal {

    fun append(typeVariable: JPTypeVariableName)

    companion object {
        fun of(
            append: (typeVariable: JPTypeVariableName) -> Unit,
        ): JavaTypeVariableContainerInternal = object : JavaTypeVariableContainerInternal {
            override fun append(typeVariable: JPTypeVariableName) = append(typeVariable)
        }
    }
}

private val JavaTypeVariableContainer.internal: JavaTypeVariableContainerInternal
    get() = when (this) {
        is JavaTypeScope -> typeVariableContainer
        is JavaMethodScope -> typeVariableContainer
    }
