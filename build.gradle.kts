@file:Suppress("PropertyName")

import java.time.Duration
import java.util.Properties
import org.apache.tools.ant.taskdefs.condition.Os

println("gradle.startParameter.taskNames: ${gradle.startParameter.taskNames}")
System.getProperties().forEach { key, value -> println("System.getProperties(): $key=$value") }
System.getenv().forEach { (key, value) -> println("System.getenv(): $key=$value") }

allprojects {
    apply(from = "$rootDir/ktlint.gradle")
}

val CI_GRADLE = "CI_GRADLE"

tasks.register("ciLint") {
    group = CI_GRADLE
    doLast {
        gradlew("ktlintCheck")
    }
}

tasks.register("ciEjabberdUnitTest") {
    group = CI_GRADLE
    doLast {
        // Stop the running container
        runExec(
            listOf("docker", "compose", "down", "-v"),
            workingDirectory = File(project.rootDir, "ejabberd")
        )

        // Start server in a new container
        runExec(
            listOf("docker", "compose", "up", "-d"),
            workingDirectory = File(project.rootDir, "ejabberd")
        )

        // Wait for server to start
        Thread.sleep(Duration.ofSeconds(3).toMillis())

        // run unit tests
        gradlew("clean", "testDebugUnitTest")
        // gradlew("cleanIosSimulatorArm64Test", "iosSimulatorArm64Test")
    }
}

tasks.register("ciTigaseUnitTest") {
    group = CI_GRADLE
    doLast {
        // Stop the running container
        runExec(
            listOf("docker", "compose", "down", "-v"),
            workingDirectory = File(project.rootDir, "tigase")
        )

        // Start server in a new container
        runExec(
            listOf("docker", "compose", "up", "-d"),
            workingDirectory = File(project.rootDir, "tigase")
        )

        // Wait for server to start
        Thread.sleep(Duration.ofSeconds(3).toMillis())

        // run unit tests
        // gradlew("clean", "testDebugUnitTest")
        // gradlew("cleanIosSimulatorArm64Test", "iosSimulatorArm64Test")
    }
}

fun runExec(commands: List<String>, workingDirectory: File? = null) {
    providers.exec {
        if (System.getenv("JAVA_HOME") == null) {
            System.getProperty("java.home")?.let { javaHome ->
                environment = environment.toMutableMap().apply {
                    put("JAVA_HOME", javaHome)
                }
            }
        }
        commandLine = commands
        workingDirectory?.also { workingDir = workingDirectory }
        println("commandLine: ${this.commandLine.joinToString(separator = " ")}")
    }.apply { println("ExecResult: ${this.result.get()}") }
}

fun gradlew(vararg tasks: String, addToSystemProperties: Map<String, String>? = null) {
    providers.exec {
        executable = File(
            project.rootDir,
            if (Os.isFamily(Os.FAMILY_WINDOWS)) "gradlew.bat" else "gradlew"
        )
            .also { it.setExecutable(true) }
            .absolutePath
        args = mutableListOf<String>().also { mutableArgs ->
            mutableArgs.addAll(tasks)
            addToSystemProperties?.toList()?.map { "-D${it.first}=${it.second}" }?.let {
                mutableArgs.addAll(it)
            }
            mutableArgs.add("--stacktrace")
        }
        val sdkDirPath = Properties().apply {
            val propertiesFile = File(rootDir, "local.properties")
            if (propertiesFile.exists()) {
                load(propertiesFile.inputStream())
            }
        }.getProperty("sdk.dir")
        if (sdkDirPath != null) {
            val platformToolsDir = "$sdkDirPath${File.separator}platform-tools"
            val pathEnvironment = System.getenv("PATH").orEmpty()
            if (!pathEnvironment.contains(platformToolsDir)) {
                environment = environment.toMutableMap().apply {
                    put("PATH", "$platformToolsDir:$pathEnvironment")
                }
            }
        }
        if (System.getenv("JAVA_HOME") == null) {
            System.getProperty("java.home")?.let { javaHome ->
                environment = environment.toMutableMap().apply {
                    put("JAVA_HOME", javaHome)
                }
            }
        }
        if (System.getenv("ANDROID_HOME") == null) {
            environment = environment.toMutableMap().apply {
                put("ANDROID_HOME", sdkDirPath)
            }
        }
        println("commandLine: ${this.commandLine}")
    }.apply { println("ExecResult: ${this.result.get()}") }
}
