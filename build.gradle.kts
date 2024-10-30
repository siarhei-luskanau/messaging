@file:Suppress("PropertyName")

import java.io.ByteArrayOutputStream
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

tasks.register("ciUnitTest") {
    group = CI_GRADLE
    doLast {
        // Stop the running container
        runCatching { runExec(listOf("docker", "stop", "ejabberd")) }

        // Stop the running container
        runCatching { runExec(listOf("docker", "rm", "-f", "ejabberd")) }

        // Start ejabberd in a new container
        runExec(
            listOf(
                "docker",
                "run",
                "--name",
                "ejabberd",
                "-d",
                "-p",
                "5222:5222",
                "ghcr.io/processone/ejabberd"
            )
        )

        // Wait for ejabberd to start
        Thread.sleep(Duration.ofSeconds(3).toMillis())

        // Register the administrator account
        runExec(
            listOf(
                "docker",
                "exec",
                "ejabberd",
                "ejabberdctl",
                "register",
                "admin",
                "localhost",
                "passw0rd"
            )
        )

        // run unit tests
        gradlew("test")

        // Check ejabberd log files
        // runExec(listOf("docker", "exec", "ejabberd", "tail", "-f", "logs/ejabberd.log"))
    }
}

fun runExec(commands: List<String>): String = object : ByteArrayOutputStream() {
    override fun write(p0: ByteArray, p1: Int, p2: Int) {
        print(String(p0, p1, p2))
        super.write(p0, p1, p2)
    }
}.let { resultOutputStream ->
    exec {
        if (System.getenv("JAVA_HOME") == null) {
            System.getProperty("java.home")?.let { javaHome ->
                environment = environment.toMutableMap().apply {
                    put("JAVA_HOME", javaHome)
                }
            }
        }
        commandLine = commands
        standardOutput = resultOutputStream
        println("commandLine: ${this.commandLine.joinToString(separator = " ")}")
    }.apply { println("ExecResult: $this") }
    String(resultOutputStream.toByteArray())
}

fun gradlew(vararg tasks: String, addToSystemProperties: Map<String, String>? = null) {
    exec {
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
    }.apply { println("ExecResult: $this") }
}
