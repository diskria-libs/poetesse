package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.EagerDelegate
import io.github.diskria.poetesse.extensions.capitalized
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.XTypeVariableName
import io.github.diskria.poetesse.interop.interopToJava

sealed interface JavaTypeVariableTrait : PoetesseJavaScope {
    operator fun XTypeVariableName.unaryPlus(): XTypeVariableName {
        this@JavaTypeVariableTrait.container.append(interopToJava())
        return this
    }
}

fun JavaTypeVariableTrait.typeVariable(
    name: String, bounds: Iterable<XTypeName> = emptyList(), nullable: Boolean = false
) = +XTypeVariableName.of(name, bounds.toList(), null, false, nullable)

fun JavaTypeVariableTrait.typeVariable(name: String, vararg bounds: XTypeName, nullable: Boolean = false) =
    typeVariable(name, bounds.asIterable(), nullable)

fun JavaTypeVariableTrait.typeVariable(bounds: Iterable<XTypeName> = emptyList(), nullable: Boolean = false) =
    EagerDelegate { typeVariable(it.capitalized(), bounds, nullable) }

fun JavaTypeVariableTrait.typeVariable(vararg bounds: XTypeName, nullable: Boolean = false) =
    EagerDelegate { typeVariable(it.capitalized(), bounds.asIterable(), nullable) }

internal class JavaTypeVariableContainer(val append: (typeVariable: JPTypeVariableName) -> Unit)

private val JavaTypeVariableTrait.container: JavaTypeVariableContainer
    get() = when (this) {
        is JavaTypeScope -> typeVariableContainer
        is JavaMethodScope -> typeVariableContainer
    }
