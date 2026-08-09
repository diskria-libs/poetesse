package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin
import io.github.diskria.poetesse.interop.XClassName

@PoetesseKotlin
class KotlinFactory(
    internal val settings: Poetesse.Settings
) : KotlinTypeFactory,
    // properties,
    KotlinConstructorFactory,
    KotlinFunctionFactory,
    KotlinParameterFactory,
    KotlinAnnotationFactory,
    KotlinCodeBlockFactory {

    fun file(packageName: String?, name: String, block: KotlinFileScope.() -> Unit): KotlinFileRef =
        KotlinFileScope.of(packageName, name).apply(block).build(settings)

    fun file(className: XClassName, block: KotlinFileScope.() -> Unit): KotlinFileRef =
        file(className.packageName, className.simpleName, block)
}
