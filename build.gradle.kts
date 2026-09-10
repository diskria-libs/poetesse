import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

projekt {
    kotlinLibrary()
    distribute {
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
