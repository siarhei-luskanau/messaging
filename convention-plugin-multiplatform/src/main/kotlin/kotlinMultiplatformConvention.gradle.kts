import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("multiplatform")
    kotlin("plugin.compose")
}

kotlin {

    androidTarget {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(
                        JvmTarget.fromTarget(
                            libs.findVersion("build-jvmTarget").get().requiredVersion
                        )
                    )
                    freeCompilerArgs.add(
                        "-Xjdk-release=${
                            JavaVersion.valueOf(
                                libs.findVersion("build-javaVersion").get().requiredVersion
                            )
                        }"
                    )
                }
            }
        }
    }

    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.runtime)
            implementation(libs.findLibrary("jetbrains-lifecycle-viewmodel-compose").get())
            implementation(libs.findLibrary("jetbrains-navigation-compose").get())
            implementation(libs.findLibrary("koin-annotations").get())
            implementation(libs.findLibrary("koin-core").get())
            implementation(libs.findLibrary("kotlinx-coroutines-core").get())
        }

        commonTest.dependencies {
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.findLibrary("kotlinx-coroutines-swing").get())
        }

        iosMain.dependencies {
        }
    }
}

android {
    compileSdk = libs.findVersion("build-android-compileSdk").get().requiredVersion.toInt()
    defaultConfig {
        minSdk = libs.findVersion("build-android-minSdk").get().requiredVersion.toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures.compose = true
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf(
            libs.findVersion("build-javaVersion").get().requiredVersion
        )
        targetCompatibility = JavaVersion.valueOf(
            libs.findVersion("build-javaVersion").get().requiredVersion
        )
    }
    packaging.resources.excludes.add("META-INF/**")
}
