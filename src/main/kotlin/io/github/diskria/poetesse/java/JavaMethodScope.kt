package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

class JavaMethodScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPMethodBuilder,
) : PoetesseJavaScope,
    JavaTypeVariableTrait,
    JavaParameterTrait,
    JavaAnnotationTrait,
    JavaModifierTrait.WithVisibility,
    JavaBodyTrait {

    internal typealias Block = JavaMethodScope.() -> Unit

    internal val typeVariableContainer = JavaTypeVariableContainerInternal(builder::addTypeVariable)
    internal val parameterContainer = JavaParameterContainer(builder::addParameter)
    internal val annotationContainer = JavaAnnotationContainer(builder::addAnnotation)
    internal val modifierContainer = JavaModifierContainerInternal(builder::addModifiers)
    internal val statementContainer = JavaBodyContainerInternal(builder::addStatement)

    fun abstract() = modifier(JPModifier.ABSTRACT)
    fun static() = modifier(JPModifier.STATIC)
    fun synchronized() = modifier(JPModifier.SYNCHRONIZED)
    fun native() = modifier(JPModifier.NATIVE)
    fun strictfp() = modifier(JPModifier.STRICTFP)

    fun returns(type: XTypeName) {
        builder.returns(type.interopToJava())
    }

    fun returns(type: KClass<*>, nullable: Boolean = false) =
        returns(xType(type, nullable = nullable))

    inline fun <reified T> returns(nullable: Boolean = true) =
        returns(T::class, nullable)

    inline fun <reified T : Any> returns() =
        returns<T>(nullable = false)

    internal fun build() = builder.build()

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(name: String) = JavaMethodScope(poetesse.config, JPMethod.methodBuilder(name))
    }
}
