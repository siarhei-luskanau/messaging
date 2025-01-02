import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("kotlinMultiplatformConvention")
    alias(libs.plugins.buildConfig)
}

android {
    namespace = "shared.xmpp.api"
}

buildConfig {
    packageName("shared.xmpp.api")
    useKotlinOutput {
        topLevelConstants = true
        internalVisibility = false
    }
    val xmppDomain = System.getProperty("XMPP_DOMAIN")
        ?: gradleLocalProperties(rootDir, providers).getProperty("XMPP_DOMAIN")
        ?: "localhost"
    buildConfigField("String", "XMPP_DOMAIN", "\"$xmppDomain\"")
    val xmppServerAddress = System.getProperty("XMPP_SERVER_ADDRESS")
        ?: gradleLocalProperties(rootDir, providers).getProperty("XMPP_SERVER_ADDRESS")
        ?: "127.0.0.1"
    buildConfigField("String", "XMPP_SERVER_ADDRESS", "\"$xmppServerAddress\"")
    val xmppServerPort = System.getProperty("XMPP_SERVER_PORT")
        ?: gradleLocalProperties(rootDir, providers).getProperty("XMPP_SERVER_PORT")
        ?: 5222
    buildConfigField("Int", "XMPP_SERVER_PORT", "$xmppServerPort")
}
