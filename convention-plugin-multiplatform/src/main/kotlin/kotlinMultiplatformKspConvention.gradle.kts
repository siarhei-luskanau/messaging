val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    id("kotlinMultiplatformConvention")
    id("com.google.devtools.ksp")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.findLibrary("koin-annotations").get())
        }
    }
}

dependencies {
    // KSP Tasks
    add("kspAndroid", libs.findLibrary("koin-ksp-compiler").get())
    add("kspCommonMainMetadata", libs.findLibrary("koin-ksp-compiler").get())
    add("kspIosArm64", libs.findLibrary("koin-ksp-compiler").get())
    add("kspIosSimulatorArm64", libs.findLibrary("koin-ksp-compiler").get())
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}
