package io.github.diskria.poetesse.kotlin

class KotlinCodeBlockRef internal constructor(build: () -> List<KotlinCodeBlockCommand>) {
    internal val commands: List<KotlinCodeBlockCommand> by lazy(build)
}

enum class KotlinCodeBlockCommandType { STATEMENT, BEGIN_CONTROL_FLOW, NEXT_CONTROL_FLOW, END_CONTROL_FLOW }
class KotlinCodeBlockCommand(val type: KotlinCodeBlockCommandType, val codeBlock: KPCodeBlock)
