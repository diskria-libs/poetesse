package io.github.diskria.poetesse.kotlin

import io.github.diskria.poetesse.PoetesseScope

sealed interface KotlinArgumentsContainer : PoetesseScope {

    fun argument(codeBlock: KPCodeBlock) {
        internal.append(codeBlock)
    }

    fun argument(name: String, nameAsComment: Boolean, value: KotlinCodeRef) {
        val prefix = if (name.isNotEmpty()) {
            if (nameAsComment) "/* $name = */ " else "$name = "
        } else {
            ""
        }
        argument(KotlinCodeScope.of(settings) { prefix + L(value) }.codeBlock)
    }

    fun argument(name: String, value: KotlinCodeRef) {
        argument(name, false, value)
    }

    fun argument(name: String, nameAsComment: Boolean, value: KotlinCodeBuilder) {
        argument(name, nameAsComment, KotlinCodeScope.of(settings, value))
    }

    fun argument(name: String, value: KotlinCodeBuilder) {
        argument(name, false, value)
    }

    fun argument(value: KotlinCodeRef) {
        argument("", false, value)
    }

    fun argument(value: KotlinCodeBuilder) {
        argument("", false, value)
    }
}

internal interface KotlinArgumentsContainerInternal {

    fun append(codeBlock: KPCodeBlock)

    companion object {
        fun of(
            append: (codeBlock: KPCodeBlock) -> Unit,
        ): KotlinArgumentsContainerInternal = object : KotlinArgumentsContainerInternal {
            override fun append(codeBlock: KPCodeBlock) = append(codeBlock)
        }
    }
}

private val KotlinArgumentsContainer.internal: KotlinArgumentsContainerInternal
    get() = when (this) {
        is KotlinTypeScope.SuperclassConstructorScope -> argumentsContainer
    }
