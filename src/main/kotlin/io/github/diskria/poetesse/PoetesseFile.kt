package io.github.diskria.poetesse

import java.nio.file.Path
import javax.annotation.processing.Filer
import kotlin.io.path.createDirectories
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

abstract class PoetesseFile {

    abstract val packageName: String?
    abstract val fileName: String
    abstract val extensionName: String
    open val relativePath: String
        get() = buildString {
            packageName?.let { append(it.replace('.', '/') + "/") }
            append("$fileName.$extensionName")
        }

    abstract fun writeTo(out: Appendable)
    abstract fun writeTo(filer: Filer)

    open fun writeTo(directory: Path): Path {
        require(directory.notExists() || directory.isDirectory()) {
            "path '$directory' exists but is not a directory."
        }
        val outputPath = directory.resolve(relativePath)
        outputPath.parent.createDirectories()
        outputPath.outputStream().bufferedWriter().use(::writeTo)
        return outputPath
    }
}
