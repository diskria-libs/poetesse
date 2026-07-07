package io.github.diskria.poetesse.kotlin

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpecHolder
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.XClassName
import io.github.diskria.poetesse.XFunctionName

@PoetesseKotlin
class KPTypeScope private constructor(
    val className: XClassName,
    private val specBuilder: TypeSpec.Builder
) : KPTypeContainerScope {

    override val typeSpecHolderBuilder: TypeSpecHolder.Builder<*> get() = specBuilder

    fun type(kind: KPTypeKind, name: String, block: KPTypeScope.() -> Unit = {}): XClassName =
        addType(kind, name, className = className.inner(name), block)

    fun function(name: String, block: KPFunctionScope.() -> Unit = {}): XFunctionName {
        specBuilder.addFunction(KPFunctionScope.of(name).apply(block).build())
        return XFunctionName.of(name)
    }

    internal fun build(): TypeSpec =
        specBuilder.build()

    internal companion object {
        fun of(kind: KPTypeKind, name: String, className: XClassName): KPTypeScope =
            KPTypeScope(
                className,
                when (kind) {
                    KPTypeKind.CLASS -> TypeSpec.classBuilder(name)
                    KPTypeKind.EXPECT_CLASS -> TypeSpec.classBuilder(name).addModifiers(KModifier.EXPECT)
                    KPTypeKind.VALUE_CLASS -> TypeSpec.classBuilder(name).addModifiers(KModifier.VALUE)
                    KPTypeKind.OBJECT -> TypeSpec.objectBuilder(name)
                    KPTypeKind.INTERFACE -> TypeSpec.interfaceBuilder(name)
                    KPTypeKind.FUN_INTERFACE -> TypeSpec.funInterfaceBuilder(name)
                    KPTypeKind.ENUM -> TypeSpec.enumBuilder(name)
                    KPTypeKind.ANNOTATION -> TypeSpec.annotationBuilder(name)
                }
            )
    }
}
