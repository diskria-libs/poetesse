package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.extensions.isBoxedVoid
import io.github.diskria.poetesse.extensions.setBoxed
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.java.JPBoxedVoid
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.java.JPVoid
import io.github.diskria.poetesse.kotlin.KPTypeName
import io.github.diskria.poetesse.kotlin.KPUnit

class XVoidTypeName private constructor(
    config: Poetesse.Config,
    override val isBoxed: Boolean,
    override val isNullable: Boolean,
) : XTypedTypeName<KPTypeName, JPTypeName>(config) {

    override fun interopToKotlinInternal(): KPTypeName = KPUnit

    override fun interopToJavaInternal(): JPTypeName = if (isBoxed) JPBoxedVoid else JPVoid

    override fun boxInternal() = of(isBoxed = true, isNullable)

    internal companion object {
        context(poetesse: PoetesseScope)
        fun of(isBoxed: Boolean, isNullable: Boolean = false) =
            XVoidTypeName(poetesse.config, isBoxed, isNullable)
    }
}

@PublishedApi
context(poetesse: PoetesseScope)
internal fun KPTypeName.asXVoidTypeNameOrNull(boxed: Boolean): XVoidTypeName? =
    if (setNullable(false).withoutAnnotations() == KPUnit) XVoidTypeName.of(isNullable || boxed, isNullable)
    else null

@PublishedApi
context(poetesse: PoetesseScope)
internal fun JPTypeName.asXVoidTypeNameOrNull(nullable: Boolean): XVoidTypeName? =
    if (setBoxed(false).withoutAnnotations() == JPVoid) XVoidTypeName.of(nullable || isBoxedVoid, nullable)
    else null
