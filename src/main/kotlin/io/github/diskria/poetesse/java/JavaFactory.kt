package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.interop.XClassName

class JavaFactory(
    override val settings: Poetesse.Settings
) : JavaTypeFactory,
    JavaFieldFactory,
    JavaConstructorFactory,
    JavaMethodFactory,
    JavaParameterFactory,
    JavaAnnotationFactory,
    JavaCodeBlockFactory {

    fun file(packageName: String?, name: String, block: JavaFileScope.Block): JavaFileRef =
        JavaFileScope.of(settings, packageName, name).apply(block).build(settings)

    fun file(className: XClassName, block: JavaFileScope.Block): JavaFileRef =
        file(className.packageName, className.simpleName, block)
}
