package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseFile
import java.nio.file.Path
import javax.annotation.processing.Filer
import kotlin.io.path.createDirectories
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

abstract class JavaFileRef : PoetesseFile {

    override val extensionName: String = "java"
    override val relativePath: String
        get() = buildString {
            packageName?.let { append(it.replace('.', '/') + "/") }
            append("$fileName.$extensionName")
        }

    override fun writeTo(directory: Path): Path {
        require(directory.notExists() || directory.isDirectory()) {
            "path '$directory' exists but is not a directory."
        }
        val outputPath = directory.resolve(relativePath)
        outputPath.parent.createDirectories()
        outputPath.outputStream().bufferedWriter().use(::writeTo)
        return outputPath
    }
}

class SingleClassJavaFileRef internal constructor(
    override val packageName: String?,
    override val fileName: String,
    private val file: JPFile,
) : JavaFileRef() {

    override fun writeTo(out: Appendable) {
        file.writeTo(out)
    }

    override fun writeTo(filer: Filer) {
        file.writeTo(filer)
    }
}

class MultiClassJavaFileRef private constructor(
    override val packageName: String?,
    override val fileName: String,
    private val types: List<JPType>,
    private val settings: Poetesse.Settings,
) : JavaFileRef() {

    private val staticImports: MutableSet<String> = mutableSetOf()
    private val imports: MutableSet<String> = mutableSetOf()
    private val typeSections: MutableList<String> = mutableListOf()

    override fun writeTo(out: Appendable) {
        settings.comment?.let {
            out.appendLine("// $it")
        }
        packageName?.let {
            out.appendLine("package $it;")
            out.appendLine()
        }
        if (staticImports.isNotEmpty()) {
            staticImports.forEach(out::appendLine)
            out.appendLine()
        }
        if (imports.isNotEmpty()) {
            imports.forEach(out::appendLine)
            out.appendLine()
        }
        out.append(typeSections.joinToString("\n\n"))
        out.appendLine()
    }

    override fun writeTo(filer: Filer) {
        val sourceFile = filer.createSourceFile(
            listOfNotNull(packageName, fileName).joinToString("."),
            *types.flatMap { it.originatingElements() }.toTypedArray()
        )
        runCatching { sourceFile.openWriter().use(::writeTo) }
            .onFailure { runCatching { sourceFile.delete() } }
            .getOrThrow()
    }

    private fun collectType(code: String) {
        val typeLines = StringBuilder()
        code.lineSequence().forEach { line ->
            when {
                line.startsWith("package ") -> Unit
                line.startsWith("import static ") -> staticImports += line
                line.startsWith("import ") -> imports += line
                else -> typeLines.appendLine(line)
            }
        }
        typeLines.toString().trim().takeIf { it.isNotEmpty() }?.let {
            typeSections += it
        }
    }

    internal companion object {
        fun mergeFrom(
            packageName: String?,
            fileName: String,
            types: List<JPType>,
            settings: Poetesse.Settings,
        ): MultiClassJavaFileRef {
            val file = MultiClassJavaFileRef(packageName, fileName, types, settings)
            types.forEach { typeSpec ->
                val source = JPFile.builder(packageName.orEmpty(), typeSpec)
                    .indent(settings.indent)
                    .build()
                    .toString()
                file.collectType(source)
            }
            return file
        }
    }
}
