package io.github.diskria.poetesse.java

import com.palantir.javapoet.TypeSpec
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName

@PoetesseJava
class JPTypeScope private constructor(
    val className: XClassName,
    private val specBuilder: TypeSpec.Builder
): JPTypeContainerScope {

    fun type(kind: JPTypeKind, name: String, block: JPTypeScope.() -> Unit = {}): XClassName =
        addType(kind, name, className = className.inner(name), block)

    override fun addType(typeSpec: TypeSpec) {
        specBuilder.addType(typeSpec)
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
