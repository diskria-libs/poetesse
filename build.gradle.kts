import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.projektor)
}

dependencies {
    api(libs.bundles.poets)
}

projekt {
    kotlinLibrary {
        jvmTarget = JvmTarget.JVM_1_8
    }
}
