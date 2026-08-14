package io.github.diskria.poetesse.interop

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.extensions.isBoxedVoid
import io.github.diskria.poetesse.extensions.setBoxed
import io.github.diskria.poetesse.extensions.setNullable
import io.github.diskria.poetesse.extensions.withoutAnnotations
import io.github.diskria.poetesse.java.JPBoxedVoid
import io.github.diskria.poetesse.java.JPTypeName
import io.github.diskria.poetesse.java.JPVoid
import io.github.diskria.poetesse.kotlin.KPTypeName
import io.github.diskria.poetesse.kotlin.KPUnit

class XVoidTypeName internal constructor(
    override val settings: Poetesse.Settings,
    override val isBoxed: Boolean,
    override val isNullable: Boolean,
) : XTypedTypeName<KPTypeName, JPTypeName>() {

    override fun interopToKotlinInternal(): KPTypeName = KPUnit

    override fun interopToJavaInternal(): JPTypeName = if (isBoxed) JPBoxedVoid else JPVoid

    override fun boxInternal(): XVoidTypeName = XVoidTypeName(settings, true, isNullable)
}

@PublishedApi
context(scope: PoetesseScope)
internal fun KPTypeName.asXVoidTypeNameOrNull(boxed: Boolean = isNullable): XVoidTypeName? =
    if (setNullable(false).withoutAnnotations() == KPUnit) {
        XVoidTypeName(scope.settings, isNullable || boxed, isNullable)
    } else null

@PublishedApi
context(scope: PoetesseScope)
internal fun JPTypeName.asXVoidTypeNameOrNull(nullable: Boolean = false): XVoidTypeName? =
    if (setBoxed(false).withoutAnnotations() == JPVoid) {
        XVoidTypeName(scope.settings, nullable || isBoxedVoid, nullable)
    } else null
