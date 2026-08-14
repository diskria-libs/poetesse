package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.XClassName

class KotlinFactory(
    override val settings: Poetesse.Settings
) : KotlinTypeFactory,
    KotlinPropertyFactory,
    KotlinConstructorFactory,
    KotlinFunctionFactory,
    KotlinParameterFactory,
    KotlinAnnotationFactory,
    KotlinCodeBlockFactory {

    fun file(packageName: String?, name: String, block: KotlinFileScope.Block = {}): KotlinFileRef =
        KotlinFileScope.of(settings, packageName, name).apply(block).build(settings)

    fun file(className: XClassName, block: KotlinFileScope.Block = {}): KotlinFileRef =
        file(className.packageName, className.simpleName, block)
}
