package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse

class KotlinFactory(
    override val config: Poetesse.Config
) : KotlinFileFactory,
    KotlinTypeFactory,
    KotlinTypeAliasFactory,
    KotlinPropertyFactory,
    KotlinConstructorFactory,
    KotlinFunctionFactory,
    KotlinContextParameterFactory,
    KotlinParameterFactory,
    KotlinAnnotationFactory,
    KotlinCodeBlockFactory
