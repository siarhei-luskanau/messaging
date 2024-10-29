rootProject.name = "Messaging"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include()

pluginManagement {
    // includeBuild("convention-plugin-multiplatform")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
