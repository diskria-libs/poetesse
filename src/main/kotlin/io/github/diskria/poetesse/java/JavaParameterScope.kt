package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.PoetesseXScope
import io.github.diskria.poetesse.interop.XTypeName
import io.github.diskria.poetesse.interop.interopToJava

class JavaParameterScope private constructor(
    override val config: Poetesse.Config,
    private val specBuilder: JPParameterBuilder,
) : PoetesseJavaScope,
    JavaAnnotationContainer,
    JavaModifierContainer {

    internal typealias Block = JavaParameterScope.() -> Unit

    internal val annotationContainer = JavaAnnotationContainerInternal.of(
        append = { specBuilder.addAnnotation(it) },
    )
    internal val modifierContainer = JavaModifierContainerInternal.of(
        append = { specBuilder.addModifiers(it) }
    )

    fun final() = modifier(JPModifier.FINAL)

    internal fun build() = specBuilder.build()

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(name: String, type: XTypeName) =
            JavaParameterScope(scope.config, JPParameter.builder(type.interopToJava(), name))
    }
}
