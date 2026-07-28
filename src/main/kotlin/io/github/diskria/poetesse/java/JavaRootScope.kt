package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName

@PoetesseJava
class JavaRootScope(
    internal val settings: Poetesse.Settings
) : JavaCodeFactory,
    JavaCodeBlockFactory,
    JavaAnnotationFactory {

    fun file(packageName: String?, name: String, block: JavaFileScope.() -> Unit): JavaDeferredFile =
        JavaFileScope.of(packageName, name).apply(block).build(settings)

    fun file(className: XClassName, block: JavaFileScope.() -> Unit): JavaDeferredFile =
        file(className.packageName, className.simpleName, block)
}
