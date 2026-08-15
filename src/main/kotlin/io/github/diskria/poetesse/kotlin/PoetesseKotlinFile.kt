package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseFile
import java.nio.file.Path
import javax.annotation.processing.Filer

class PoetesseKotlinFile internal constructor(private val spec: KPFile) : PoetesseFile {

    override val packageName: String? = spec.packageName.takeIf { it.isNotEmpty() }
    override val fileName: String = spec.name
    override val extensionName: String = "kt"
    override val relativePath: String = spec.relativePath

    override fun writeTo(out: Appendable) {
        spec.writeTo(out)
    }

    override fun writeTo(directory: Path): Path =
        spec.writeTo(directory)

    override fun writeTo(filer: Filer) {
        spec.writeTo(filer)
    }
}
