package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.applyCodeBlockMutation
import io.github.diskria.poetesse.interop.PoetesseScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava
import io.github.diskria.poetesse.interop.xType
import kotlin.reflect.KClass

class JavaMethodScope private constructor(
    override val config: Poetesse.Config,
    private val builder: JPMethodBuilder,
) : PoetesseJavaScope,
    JavaDocumentationTrait,
    JavaAnnotationTrait,
    JavaModifierTrait.WithVisibility,
    JavaTypeVariableTrait,
    JavaParameterTrait,
    JavaBodyTrait {

    internal typealias Block = JavaMethodScope.() -> Unit

    internal val documentationContainer by lazy { JavaDocumentationContainer(builder::addJavadoc) }
    internal val annotationContainer by lazy { JavaAnnotationContainer(builder::addAnnotation) }
    internal val modifierContainer by lazy { JavaModifierContainer(builder::addModifiers) }
    internal val typeVariableContainer by lazy { JavaTypeVariableContainer(builder::addTypeVariable) }
    internal val parameterContainer by lazy { JavaParameterContainer(builder::addParameter) }
    internal val statementContainer by lazy { JavaBodyContainer(builder::applyCodeBlockMutation) }

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
