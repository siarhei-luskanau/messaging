plugins {
    id("kotlinMultiplatformConvention")
    alias(libs.plugins.google.ksp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        jvmMain.dependencies {
            implementation(libs.halcyon.core)
            implementation(libs.javax.jaxb.api)
            implementation(libs.rocks.xmpp.core.client)
            implementation(libs.rocks.xmpp.extensions.client)
            implementation(libs.sun.jaxb.core)
            implementation(libs.sun.jaxb.impl)
        }
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "shared.common"
}

dependencies {
    ksp(libs.koin.ksp.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}
