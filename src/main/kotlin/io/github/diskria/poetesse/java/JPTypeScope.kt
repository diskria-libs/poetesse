package io.github.diskria.poetesse.java

import com.palantir.javapoet.TypeSpec
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName

@PoetesseJava
class JPTypeScope private constructor(
    val className: XClassName,
    private val specBuilder: TypeSpec.Builder
): JPTypeContainerScope {

    internal val typeContainerInternalScope = object : JPTypeContainerScope.Companion.Internal {
        override fun innerClassName(name: String): XClassName =
            className.inner(name)

        override fun addType(typeSpec: TypeSpec) {
            specBuilder.addType(typeSpec)
        }
    }

    internal fun build(): TypeSpec =
        specBuilder.build()

    internal companion object {
        fun of(kind: JPTypeKind, name: String, className: XClassName): JPTypeScope =
            JPTypeScope(
                className,
                when (kind) {
                    JPTypeKind.CLASS -> TypeSpec.classBuilder(name)
                    JPTypeKind.RECORD -> TypeSpec.recordBuilder(name)
                    JPTypeKind.INTERFACE -> TypeSpec.interfaceBuilder(name)
                    JPTypeKind.ENUM -> TypeSpec.enumBuilder(name)
                    JPTypeKind.ANNOTATION -> TypeSpec.annotationBuilder(name)
                }
            )
    }
}
