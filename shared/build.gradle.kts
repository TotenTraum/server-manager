plugins {
    kotlin("multiplatform") version "1.9.25"
    kotlin("plugin.serialization") version "1.9.25"
}

kotlin {
    jvm {
    }
    js(IR){
        browser {
            commonWebpackConfig{
                outputFileName = "shared.bundle.js"
            }
        }
        binaries.library()
    }
    sourceSets{
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
        }
    }
}