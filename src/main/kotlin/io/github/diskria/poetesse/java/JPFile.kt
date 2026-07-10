package io.github.diskria.poetesse.java

import com.palantir.javapoet.JavaFile
import com.palantir.javapoet.TypeSpec
import io.github.diskria.poetesse.Poetesse
import io.github.diskria.poetesse.PoetesseFile
import java.nio.file.Path
import javax.annotation.processing.Filer
import kotlin.io.path.createDirectories
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

abstract class JPFile : PoetesseFile {

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

class SingleClassJPFile(
    override val packageName: String?,
    override val fileName: String,
    private val javaFile: JavaFile,
) : JPFile() {

    override fun writeTo(out: Appendable) {
        javaFile.writeTo(out)
    }

    override fun writeTo(filer: Filer) {
        javaFile.writeTo(filer)
    }
}

class MultiClassJPFile private constructor(
    override val packageName: String?,
    override val fileName: String,
    private val typeSpecs: List<TypeSpec>,
    private val settings: Poetesse.Settings,
) : JPFile() {

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
            *typeSpecs.flatMap { it.originatingElements() }.toTypedArray()
        )
        runCatching { sourceFile.openWriter().use(::writeTo) }
            .onFailure { runCatching { sourceFile.delete() } }
            .getOrThrow()
    }

    private enum class Phase {
        PACKAGE, STATIC_IMPORTS, IMPORTS, TYPE;

        companion object {
            fun detect(text: String): Phase? =
                when (text) {
                    "package " -> PACKAGE
                    "import static " -> STATIC_IMPORTS
                    "import " -> IMPORTS
                    else -> null
                }
        }
    }

    private inner class TypeCollector : Appendable {

        private val codeBuffer: StringBuilder = StringBuilder()

        private var currentPhase: Phase = Phase.PACKAGE
        private var newLineCounter: Int = 0
        private var isFirstChunk: Boolean = true
        private var isSectionFirstChunk: Boolean = true

        fun commit() {
            if (codeBuffer.isNotEmpty()) {
                typeSections.add(codeBuffer.toString().trim())
                codeBuffer.clear()
            }
            currentPhase = Phase.PACKAGE
            newLineCounter = 0
            isSectionFirstChunk = true
            isFirstChunk = true
        }

        override fun append(text: CharSequence?): Appendable {
            if (text != null) processChunk(text.toString())
            return this
        }

        override fun append(text: CharSequence?, start: Int, end: Int): Appendable {
            if (text != null) processChunk(text.subSequence(start, end).toString())
            return this
        }

        override fun append(char: Char): Appendable {
            processChunk(char.toString())
            return this
        }

        private fun processChunk(text: String) {
            if (currentPhase != Phase.TYPE) {
                val detectedPhase = Phase.detect(text)
                detectedPhase?.let {
                    currentPhase = it
                }
                if (text == "\n") {
                    if (newLineCounter == 0) {
                        if (currentPhase == Phase.STATIC_IMPORTS) {
                            staticImports.add(codeBuffer.toString())
                            codeBuffer.clear()
                        } else if (currentPhase == Phase.IMPORTS) {
                            imports.add(codeBuffer.toString())
                            codeBuffer.clear()
                        }
                    }
                    newLineCounter++
                    return
                }
                isSectionFirstChunk = isFirstChunk || newLineCounter == 2
                newLineCounter = 0
                if (isSectionFirstChunk && detectedPhase == null) {
                    codeBuffer.clear()
                    currentPhase = Phase.TYPE
                }
            }
            if (currentPhase != Phase.PACKAGE) {
                codeBuffer.append(text)
            }
            isFirstChunk = false
        }
    }

    companion object {
        fun mergeFrom(
            packageName: String?,
            fileName: String,
            typeSpecs: List<TypeSpec>,
            settings: Poetesse.Settings,
        ): MultiClassJPFile {
            val file = MultiClassJPFile(packageName, fileName, typeSpecs, settings)
            val typeCollector = file.TypeCollector()
            typeSpecs.forEach { typeSpec ->
                val virtualFile = JavaFile.builder(packageName.orEmpty(), typeSpec).apply {
                    indent(settings.indent)
                }.build()
                virtualFile.writeTo(typeCollector)
                typeCollector.commit()
            }
            return file
        }
    }
}
