plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.languageVersion.get().toInt())
    }
}

dependencies {
    implementation(libs.halcyon.core)
    implementation(libs.javax.jaxb.api)
    implementation(libs.rocks.xmpp.core.client)
    implementation(libs.rocks.xmpp.extensions.client)
    implementation(libs.sun.jaxb.core)
    implementation(libs.sun.jaxb.impl)
    testImplementation(libs.kotlin.test)
}
