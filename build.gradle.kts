import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.projektor)
}

dependencies {
    api(libs.bundles.poets)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

projekt {
    kotlinLibrary {
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
