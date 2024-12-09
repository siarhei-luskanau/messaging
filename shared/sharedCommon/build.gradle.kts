plugins {
    id("kotlinMultiplatformKspConvention")
}

kotlin {
    sourceSets {
        jvmMain.dependencies {
            implementation(libs.halcyon.core)
            implementation(libs.smack.sasl.provided)
            implementation(libs.smack.java8.full)
            implementation(libs.smack.tcp)
            implementation(libs.smack.xmlparser.xpp3)
        }
    }
}

android {
    namespace = "shared.common"
}
