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

    fun static() {
        modifiers(JPModifier.STATIC)
    }

    fun body(block: BodyScope.() -> Unit) {
        BodyScope().apply(block)
    }

    fun returnType(type: XTypeName, interop: Boolean = true) {
        specBuilder.returns(type.toJava(interop))
    }

    fun returnType(type: KClass<out Any>, nullable: Boolean = false, interop: Boolean = true) =
        returnType(type.asXTypeName().setNullable(nullable), interop)

    inline fun <reified T : Any> returnType(nullable: Boolean = false, interop: Boolean = true) =
        returnType(T::class, nullable, interop)

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
