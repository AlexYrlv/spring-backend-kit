plugins {
    // Allows Gradle to auto-download the JDK declared by the toolchain
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "spring-backend-kit"

include("sample-service")
