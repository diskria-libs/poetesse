package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.interop.XClassName

@PoetesseJava
class JavaFactory(
    internal val settings: Poetesse.Settings
) : JavaTypeFactory,
    JavaFieldFactory,
    JavaMethodFactory,
    JavaAnnotationFactory,
    JavaCodeBlockFactory {

    fun file(packageName: String?, name: String, block: JavaFileScope.() -> Unit): JavaFileRef =
        JavaFileScope.of(packageName, name).apply(block).build(settings)

    fun file(className: XClassName, block: JavaFileScope.() -> Unit): JavaFileRef =
        file(className.packageName, className.simpleName, block)
}
