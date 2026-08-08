package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.asXTypeName
import io.github.diskria.poetesse.interop.setNullable
import kotlin.reflect.KClass

@PoetesseJava
class JavaMethodScope private constructor(
    private val specBuilder: JPMethodBuilder
) : JavaParameterContainer,
    JavaAnnotationContainer,
    JavaFinalWithVisibilityModifierContainer {

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
        BodyScope().apply(block)
    }

    fun returnType(type: XTypeName) {
        specBuilder.returns(type.interopToJava())
    }

    fun returnType(type: KClass<out Any>, nullable: Boolean = false) =
        returnType(type.asXTypeName().setNullable(nullable))

    inline fun <reified T : Any> returnType(nullable: Boolean = false) =
        returnType(T::class, nullable)

    internal fun build(): JPMethod =
        specBuilder.build()

    inner class BodyScope : JavaCodeBlockContainer {
        internal val codeBlockContainer = JavaCodeBlockContainerInternal.of(
            append = { specBuilder.addStatement(it) }
        )
    }

    internal companion object {
        fun of(name: String): JavaMethodScope =
            JavaMethodScope(JPMethod.methodBuilder(name))
    }
}
