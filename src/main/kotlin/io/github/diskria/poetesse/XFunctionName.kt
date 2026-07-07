package io.github.diskria.poetesse

class XFunctionName private constructor(name: String, jvmName: String) {

    internal val kotlin: String = name
    internal val java: String = jvmName

    companion object {
        fun of(name: String, jvmName: String = name): XFunctionName =
            XFunctionName(name, jvmName)
    }
}
