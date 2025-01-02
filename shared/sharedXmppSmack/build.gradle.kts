plugins {
    id("kotlinMultiplatformKspConvention")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.smack.android.extensions)
            implementation(libs.smack.tcp)
            implementation(projects.shared.sharedXmppApi)
        }
    }
}

android {
    namespace = "shared.xmpp.smack"
    testOptions.configureTestOptions()
}

configurations {
    all {
        exclude(group = "xpp3", module = "xpp3")
        exclude(group = "xpp3", module = "xpp3_min")
    }
}
