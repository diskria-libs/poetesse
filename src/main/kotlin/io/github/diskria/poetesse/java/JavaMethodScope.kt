package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava
import io.github.diskria.poetesse.xType
import kotlin.reflect.KClass

@PoetesseJava
class JavaMethodScope private constructor(
    override val settings: Poetesse.Settings,
    private val specBuilder: JPMethodBuilder
) : JavaTypeVariableContainer,
    JavaParameterContainer,
    JavaAnnotationContainer,
    JavaModifierContainer.WithVisibility {

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
        append = { specBuilder.addModifiers(*it) }
    )

    fun abstract() {
        modifiers(JPModifier.ABSTRACT)
    }

    fun static() {
        modifiers(JPModifier.STATIC)
    }

    fun synchronized() {
        modifiers(JPModifier.SYNCHRONIZED)
    }

    fun native() {
        modifiers(JPModifier.NATIVE)
    }

    fun strictfp() {
        modifiers(JPModifier.STRICTFP)
    }

    fun body(block: BodyScope.() -> Unit) {
        BodyScope(settings).apply(block)
    }

    fun returns(type: XTypeName<*, *>) {
        specBuilder.returns(type.interopToJava())
    }

    fun returns(type: KClass<*>, nullable: Boolean = false) =
        returns(xType(type, nullable = nullable))

    inline fun <reified T> returns(nullable: Boolean = true) =
        returns(T::class, nullable)

    inline fun <reified T : Any> returns() =
        returns<T>(nullable = false)

    internal fun build(): JPMethod =
        specBuilder.build()

    @PoetesseJava
    inner class BodyScope(override val settings: Poetesse.Settings) : JavaCodeBlockContainer {
        internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
            append = { this@JavaMethodScope.specBuilder.addStatement(it) }
        )
    }

    internal companion object {
        fun of(settings: Poetesse.Settings, name: String): JavaMethodScope =
            JavaMethodScope(settings, JPMethod.methodBuilder(name))
    }
}
