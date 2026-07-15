package io.github.diskria.poetesse

import java.nio.file.Path
import javax.annotation.processing.Filer

interface PoetesseFile {
    val packageName: String?
    val fileName: String
    val extensionName: String
    val relativePath: String

    fun writeTo(out: Appendable)
    fun writeTo(directory: Path): Path
    fun writeTo(filer: Filer)
}
