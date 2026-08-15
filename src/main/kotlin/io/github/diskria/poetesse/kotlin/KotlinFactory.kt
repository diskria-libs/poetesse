package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse

class KotlinFactory(
    override val config: Poetesse.Config
) : KotlinFileFactory,
    KotlinTypeFactory,
    KotlinPropertyFactory,
    KotlinConstructorFactory,
    KotlinFunctionFactory,
    KotlinParameterFactory,
    KotlinAnnotationFactory,
    KotlinCodeBlockFactory
