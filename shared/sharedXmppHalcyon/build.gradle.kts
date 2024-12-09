plugins {
    id("kotlinMultiplatformKspConvention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.halcyon.core)
            implementation(projects.shared.sharedXmppApi)
        }
    }
}

android {
    namespace = "shared.xmpp.halcyon"
    testOptions.configureTestOptions()
}
