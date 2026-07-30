package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.PoetesseJava

@PoetesseJava
class JavaMethodScope private constructor(
    private val specBuilder: JPMethodBuilder
) : JavaModifierConfigScope.External,
    JavaAnnotationConfigScope.External {

    internal val modifierConfigInternalScope = JavaModifierConfigScope.Internal.of(
        append = { specBuilder.addModifiers(*it) }
    )
    internal val annotationConfigInternalScope = JavaAnnotationConfigScope.Internal.of(
        append = { specBuilder.addAnnotation(it.spec) },
    )

    fun body(block: Body.() -> Unit) {
        Body().apply(block)
    }

    fun static() {
        modifiers(JPModifier.STATIC)
    }

    internal fun build(): JPMethod =
        specBuilder.build()

    inner class Body : JavaCodeBlockContainerScope.External {
        internal val codeBlockContainerInternalScope = JavaCodeBlockContainerScope.Internal.of(
            append = { specBuilder.addStatement(it) }
        )
    }

    internal companion object {
        fun of(name: String): JavaMethodScope =
            JavaMethodScope(JPMethod.methodBuilder(name))
    }
}
