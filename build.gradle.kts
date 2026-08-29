import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(convention.plugins.projektor)
}

projekt {
    kotlinLibrary()
    distribute {
        mavenLocal()
        mavenCentral()
    }
}

dependencies {
    api(libs.bundles.poets)
}

tasks {
    withType<KotlinCompile>().configureEach {
        compilerOptions {
            optIn.add("com.squareup.kotlinpoet.ExperimentalKotlinPoetApi")
        }
    }
}
