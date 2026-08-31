pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor") version "8.0.12"
}

projekt {
    version = "0.1.2"
    license { mit() }
    kotlinLibrary()
}
