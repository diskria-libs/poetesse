package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

class JavaMethodScope private constructor(
    override val config: Poetesse.Config,
    private val specBuilder: JPMethodBuilder,
) : PoetesseJavaScope,
    JavaTypeVariableContainer,
    JavaParameterContainer,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility,
    JavaBodyContainer {

    internal typealias Block = JavaMethodScope.() -> Unit

    internal val typeVariableContainer = JavaTypeVariableContainerInternal.of(
        append = { specBuilder.addTypeVariable(it) }
    )
    internal val parameterContainer = JavaParameterContainerInternal.of(
        append = { specBuilder.addParameter(it) }
    )
    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(it) }
    )
    internal val statementContainer = JavaBodyContainerInternal.of(
        append = { specBuilder.addStatement(it) }
    )

    fun abstract() = modifier(JPModifier.ABSTRACT)
    fun static() = modifier(JPModifier.STATIC)
    fun synchronized() = modifier(JPModifier.SYNCHRONIZED)
    fun native() = modifier(JPModifier.NATIVE)
    fun strictfp() = modifier(JPModifier.STRICTFP)

    fun returns(type: XTypeName) {
        specBuilder.returns(type.interopToJava())
    }

    fun returns(type: KClass<*>, nullable: Boolean = false) =
        returns(xType(type, nullable = nullable))

    inline fun <reified T> returns(nullable: Boolean = true) =
        returns(T::class, nullable)

    inline fun <reified T : Any> returns() =
        returns<T>(nullable = false)

    internal fun build() = specBuilder.build()

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(name: String) = JavaMethodScope(scope.config, JPMethod.methodBuilder(name))
    }
}
