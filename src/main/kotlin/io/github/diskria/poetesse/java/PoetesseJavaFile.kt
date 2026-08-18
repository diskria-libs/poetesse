package io.github.diskria.poetesse.java

import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseFile
import io.github.diskria.poetesse.extensions.joinWithTrailing
import io.github.diskria.poetesse.interop.PoetesseScope
import java.nio.file.Path
import javax.annotation.processing.Filer
import kotlin.io.path.createDirectories
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

class PoetesseJavaFile private constructor(
    private val config: Poetesse.Config,
    override val packageName: String?,
    override val fileName: String,
    private val types: List<JPType>,
    extraImports: Set<String> = emptySet(),
    extraStaticImports: Set<String> = emptySet(),
) : PoetesseFile {

    override val extensionName: String = "java"

    override val relativePath: String = buildString {
        packageName?.split('.')?.joinWithTrailing("/")?.let(::append)
        append("$fileName.$extensionName")
    }

    private val staticImports: MutableSet<String> = extraStaticImports.toMutableSet()
    private val imports: MutableSet<String> = extraImports.toMutableSet()
    private val typeSections: MutableList<String> = mutableListOf()

    init {
        types.forEach { typeSpec ->
            collectType(
                JPFile.builder(packageName.orEmpty(), typeSpec).apply {
                    indent(config.indent)
                    skipJavaLangImports(config.skipLangDefaultImports)
                }.build().toString()
            )
        }
    }

    override fun writeTo(out: Appendable) {
        config.comment?.let {
            out.appendLine("// $it")
        }
        packageName?.let {
            out.appendLine("package $it;")
            out.appendLine()
        }
        if (staticImports.isNotEmpty()) {
            staticImports.forEach { out.appendLine(staticImportAffix.wrap(it)) }
            out.appendLine()
        }
        if (imports.isNotEmpty()) {
            imports.forEach { out.appendLine(importAffix.wrap(it)) }
            out.appendLine()
        }
        out.append(typeSections.joinToString("\n\n"))
        out.appendLine()
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

    override fun writeTo(filer: Filer) {
        val sourceFile = filer.createSourceFile(
            buildString {
                packageName?.let { append("$it.") }
                append(fileName)
            },
            *types.flatMap { it.originatingElements() }.toTypedArray()
        )
        runCatching { sourceFile.openWriter().use(::writeTo) }
            .onFailure { runCatching { sourceFile.delete() } }
            .getOrThrow()
    }

    private fun collectType(code: String) {
        val typeLines = StringBuilder()
        code.lineSequence().forEach { line ->
            if (packageAffix.matches(line)) return@forEach
            staticImportAffix.unwrapOrNull(line)?.let { staticImports += it }
                ?: importAffix.unwrapOrNull(line)?.let { imports += it }
                ?: typeLines.appendLine(line)
        }
        typeLines.toString().trim().ifEmpty { null }?.let {
            typeSections += it
        }
    }

    internal companion object {
        private val packageAffix = StringAffix(prefix = "package ", suffix = ";")
        private val staticImportAffix = StringAffix(prefix = "import static ", suffix = ";")
        private val importAffix = StringAffix(prefix = "import ", suffix = ";")

        context(poetesse: PoetesseScope)
        fun of(
            packageName: String?,
            fileName: String,
            types: List<JPType>,
            extraImports: Set<String>,
            extraStaticImports: Set<String>,
        ) = PoetesseJavaFile(poetesse.config, packageName, fileName, types, extraImports, extraStaticImports)
    }
}

private class StringAffix(val prefix: String = "", val suffix: String = "") {

    fun wrap(value: String): String =
        "$prefix$value$suffix"

    fun matches(value: String): Boolean =
        value.startsWith(prefix) && value.endsWith(suffix)

    fun unwrapOrNull(value: String): String? =
        if (!matches(value)) null
        else value.removePrefix(prefix).removeSuffix(suffix)
}
