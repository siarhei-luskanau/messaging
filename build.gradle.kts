@file:Suppress("PropertyName")

import java.io.InputStream
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
        gradlew("ktlintCheck", "lint")
    }
}

tasks.register("ciEjabberdUnitTest") {
    group = CI_GRADLE
    doLast {
        launchOnEjabberd {
            launchUnitTest()
        }
    }
}

tasks.register("ciEjabberdAndroidTest") {
    group = CI_GRADLE
    doLast {
        launchOnEjabberd {
            launchAndroidTest()
        }
    }
}

tasks.register("ciTigaseUnitTest") {
    group = CI_GRADLE
    doLast {
        launchOnTigase {
            // launchUnitTest()
        }
    }
}

tasks.register("ciTigaseAndroidTest") {
    group = CI_GRADLE
    doLast {
        launchOnTigase {
            // launchAndroidTest()
        }
    }
}

fun launchUnitTest() {
    // run unit tests
    gradlew("clean", "testDebugUnitTest")
    // gradlew("cleanIosSimulatorArm64Test", "iosSimulatorArm64Test")
}

fun launchAndroidTest() {
    // run android tests
    if (true.toString().equals(other = System.getenv("CI"), ignoreCase = true)) {
        gradlew(
            "clean",
            "managedVirtualDeviceDebugAndroidTest",
            "-Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect"
        )
    } else {
        gradlew(
            "clean",
            "managedVirtualDeviceDebugAndroidTest",
            "-Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect",
            "--enable-display"
        )
    }
}

fun launchOnEjabberd(tests: () -> Unit) {
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

    tests.invoke()
}

fun launchOnTigase(tests: () -> Unit) {
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

    tests.invoke()
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

tasks.register("ciSdkManagerLicenses") {
    group = CI_GRADLE
    doLast {
        val sdkDirPath = getAndroidSdkPath(rootDir = rootDir)
        getSdkManagerFile(rootDir = rootDir)?.let { sdkManagerFile ->
            val yesInputStream = object : InputStream() {
                private val yesString = "y\n"
                private var counter = 0
                override fun read(): Int = yesString[counter % 2].also { counter++ }.code
            }
            providers.exec {
                executable = sdkManagerFile.absolutePath
                args = listOf("--list", "--sdk_root=$sdkDirPath")
                println("exec: ${this.commandLine.joinToString(separator = " ")}")
            }.apply { println("ExecResult: ${this.result.get()}") }
            @Suppress("DEPRECATION")
            exec {
                executable = sdkManagerFile.absolutePath
                args = listOf("--licenses", "--sdk_root=$sdkDirPath")
                standardInput = yesInputStream
                println("exec: ${this.commandLine.joinToString(separator = " ")}")
            }.apply { println("ExecResult: $this") }
        }
    }
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

fun getAndroidSdkPath(rootDir: File): String? = Properties().apply {
    val propertiesFile = File(rootDir, "local.properties")
    if (propertiesFile.exists()) {
        load(propertiesFile.inputStream())
    }
}.getProperty("sdk.dir").let { propertiesSdkDirPath ->
    (propertiesSdkDirPath ?: System.getenv("ANDROID_HOME"))
}

fun getSdkManagerFile(rootDir: File): File? =
    getAndroidSdkPath(rootDir = rootDir)?.let { sdkDirPath ->
        println("sdkDirPath: $sdkDirPath")
        val files = File(sdkDirPath).walk().filter { file ->
            file.path.contains("cmdline-tools") && file.path.endsWith("sdkmanager")
        }
        files.forEach { println("walk: ${it.absolutePath}") }
        val sdkmanagerFile = files.firstOrNull()
        println("sdkmanagerFile: $sdkmanagerFile")
        sdkmanagerFile
    }
