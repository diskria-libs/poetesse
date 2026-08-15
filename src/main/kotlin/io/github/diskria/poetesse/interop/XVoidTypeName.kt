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

    override fun boxInternal(): XVoidTypeName = XVoidTypeName(config, true, isNullable)

    internal companion object {
        context(scope: PoetesseXScope)
        fun of(isBoxed: Boolean, isNullable: Boolean) =
            XVoidTypeName(scope.config, isBoxed, isNullable)
    }
}

@PublishedApi
context(scope: PoetesseXScope)
internal fun KPTypeName.asXVoidTypeNameOrNull(boxed: Boolean = isNullable): XVoidTypeName? =
    if (setNullable(false).withoutAnnotations() == KPUnit) XVoidTypeName.of(isNullable || boxed, isNullable)
    else null

@PublishedApi
context(scope: PoetesseXScope)
internal fun JPTypeName.asXVoidTypeNameOrNull(nullable: Boolean = false): XVoidTypeName? =
    if (setBoxed(false).withoutAnnotations() == JPVoid) XVoidTypeName.of(nullable || isBoxedVoid, nullable)
    else null
