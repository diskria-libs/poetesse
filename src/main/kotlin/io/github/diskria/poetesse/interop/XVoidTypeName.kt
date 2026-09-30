package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.isBoxedVoid
import io.github.diskria.poetesse.extensions.setBoxed
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.java.JPBoxedVoid
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.java.JPVoid
import io.github.diskria.poetesse.kotlin.KPNothing
import io.github.diskria.poetesse.kotlin.KPTypeName
import io.github.diskria.poetesse.kotlin.KPUnit

class XVoidTypeName private constructor(
    config: Poetesse.Config,
    val isNothing: Boolean,
    override val isBoxed: Boolean,
    override val isNullable: Boolean,
) : XTypedTypeName<KPTypeName, JPTypeName>(config) {

    override fun interopToKotlinInternal(): KPTypeName = if (isNothing) KPNothing else KPUnit

    override fun interopToJavaInternal(): JPTypeName = if (isBoxed) JPBoxedVoid else JPVoid

    override fun boxInternal() = of(isNothing, isBoxed = true, isNullable)

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(isNothing: Boolean, isBoxed: Boolean, isNullable: Boolean = false) =
            XVoidTypeName(poetesse.config, isNothing, isBoxed, isNullable)
    }
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPTypeName.asXVoidTypeNameOrNull(boxed: Boolean): XVoidTypeName? {
    val cleanType = setNullable(false).withoutAnnotations()
    val isNothing = cleanType == KPNothing
    val isUnit = cleanType == KPUnit
    if (!isUnit && !isNothing) return null
    if (isUnit && isNullable) return null
    return XVoidTypeName.of(
        isNothing = isNothing,
        isBoxed = isNothing || boxed,
        isNullable = isNullable,
    )
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPTypeName.asXVoidTypeNameOrNull(nullable: Boolean): XVoidTypeName? {
    val isBoxed = nullable || isBoxedVoid
    val cleanType = setBoxed(false).withoutAnnotations()
    if (cleanType != JPVoid) return null
    return XVoidTypeName.of(
        isNothing = isBoxed,
        isBoxed = isBoxed,
        isNullable = nullable,
    )
}
