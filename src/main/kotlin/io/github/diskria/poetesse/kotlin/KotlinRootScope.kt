package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseKotlin

@PoetesseKotlin
class KotlinRootScope(internal val settings: Poetesse.Settings) {

    fun file(packageName: String?, fileName: String, block: KotlinFileScope.() -> Unit): KotlinFileRef =
        KotlinFileScope.of(packageName, fileName).apply(block).build(settings)
}
