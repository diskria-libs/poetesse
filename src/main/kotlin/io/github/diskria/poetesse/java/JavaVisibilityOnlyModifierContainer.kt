package io.github.diskria.poetesse.java

sealed interface JavaVisibilityOnlyModifierContainer : JavaModifierContainer {

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
}
