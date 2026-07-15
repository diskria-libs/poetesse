package io.github.diskria.poetesse.java

class JavaModifierConfigScope private constructor() {

    sealed interface External {

        fun modifiers(vararg modifiers: JPModifier) {
            internal.append(*modifiers)
        }

        fun visibility(visibility: JavaVisibility) {
            if (visibility == JavaVisibility.PACKAGE_PRIVATE) return
            modifiers(
                when (visibility) {
                    JavaVisibility.PUBLIC -> JPModifier.PUBLIC
                    JavaVisibility.PROTECTED -> JPModifier.PROTECTED
                    JavaVisibility.PRIVATE -> JPModifier.PRIVATE
                }
            )
        }

        fun public() {
            visibility(JavaVisibility.PUBLIC)
        }

        fun protected() {
            visibility(JavaVisibility.PROTECTED)
        }

        fun packagePrivate() {
            visibility(JavaVisibility.PACKAGE_PRIVATE)
        }

        fun private() {
            visibility(JavaVisibility.PRIVATE)
        }

        fun final() {
            modifiers(JPModifier.FINAL)
        }
    }

    @PublishedApi internal interface Internal {

        fun append(vararg modifiers: JPModifier)

        companion object {
            internal fun of(
                append: (modifiers: Array<out JPModifier>) -> Unit,
            ): Internal = object : Internal {
                override fun append(vararg modifiers: JPModifier) = append(modifiers)
            }
        }
    }
}

internal val JavaModifierConfigScope.External.internal: JavaModifierConfigScope.Internal
    get() = when (this) {
        is JavaTypeScope -> modifierConfigInternalScope
        is JavaMethodScope -> modifierConfigInternalScope
    }
