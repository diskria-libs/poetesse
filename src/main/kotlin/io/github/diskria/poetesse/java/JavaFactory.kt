package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse

class JavaFactory(
    override val config: Poetesse.Config
) : JavaFileFactory,
    JavaTypeFactory,
    JavaFieldFactory,
    JavaConstructorFactory,
    JavaMethodFactory,
    JavaParameterFactory,
    JavaAnnotationFactory,
    JavaCodeBlockFactory
