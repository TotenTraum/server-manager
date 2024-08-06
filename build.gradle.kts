plugins {
    kotlin("multiplatform") version "1.9.25" apply false
    kotlin("plugin.serialization") version "1.9.25" apply false
    kotlin("jvm") version "1.9.25" apply false
    id("io.ktor.plugin") version "2.3.12" apply false
    id("org.graalvm.buildtools.native") version "0.10.2" apply false
}