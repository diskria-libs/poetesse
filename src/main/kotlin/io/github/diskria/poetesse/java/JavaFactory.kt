package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseJava
import io.github.diskria.poetesse.PoetesseScope
import io.github.diskria.poetesse.interop.XClassName

@PoetesseJava
class JavaFactory(override val settings: Poetesse.Settings) : PoetesseScope,
    JavaTypeFactory,
    JavaFieldFactory,
    JavaConstructorFactory,
    JavaMethodFactory,
    JavaParameterFactory,
    JavaAnnotationFactory,
    JavaCodeBlockFactory {

    fun file(packageName: String?, name: String, block: JavaFileScope.() -> Unit): JavaFileRef =
        JavaFileScope.of(settings, packageName, name).apply(block).build(settings)

    fun file(className: XClassName, block: JavaFileScope.() -> Unit): JavaFileRef =
        file(className.packageName, className.simpleName, block)
}
