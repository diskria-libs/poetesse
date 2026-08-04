package io.github.diskria.poetesse.java

sealed interface JavaFinalOnlyModifierContainer : JavaModifierContainer {
    fun final() {
        modifiers(JPModifier.FINAL)
    }
}
