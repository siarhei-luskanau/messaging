rootProject.name = "Messaging"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(
    "shared:sharedCommon"
)

pluginManagement {
    includeBuild("convention-plugin-multiplatform")
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
        maven("https://maven-repo.tigase.org/repository/snapshot")
    }
}
