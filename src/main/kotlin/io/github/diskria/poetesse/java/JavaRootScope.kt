package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.XClassName

@PoetesseJava
class JavaRootScope(
    internal val settings: Poetesse.Settings
) : JavaCodeFactory,
    JavaCodeBlockFactory,
    JavaAnnotationFactory,
    JavaTypeFactory,
    JavaMethodFactory {

    fun file(packageName: String?, name: String, block: JavaFileScope.() -> Unit): JavaPoetesseFile =
        JavaFileScope.of(packageName, name).apply(block).build(settings)

    fun file(className: XClassName, block: JavaFileScope.() -> Unit): JavaPoetesseFile =
        file(className.packageName, className.simpleName, block)
}
