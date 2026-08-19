import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.projektor)
}

dependencies {
    api(libs.bundles.poets)
}

projekt {
    kotlinLibrary {
        jvmTarget = JvmTarget.JVM_17
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-XXLanguage:+ContextParameters"))
    optIn.add("com.squareup.kotlinpoet.ExperimentalKotlinPoetApi")
}
