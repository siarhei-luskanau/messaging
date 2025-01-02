import com.android.build.api.dsl.ManagedVirtualDevice
import com.android.build.api.dsl.TestOptions
import org.gradle.kotlin.dsl.create

fun TestOptions.configureTestOptions() {
    unitTests {
        all { test: org.gradle.api.tasks.testing.Test ->
            test.testLogging {
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                events = org.gradle.api.tasks.testing.logging.TestLogEvent.values().toSet()
            }
        }
    }
    animationsDisabled = true
    emulatorSnapshots {
        enableForTestFailures = false
    }
    managedDevices.devices.create<ManagedVirtualDevice>("managedVirtualDevice") {
        device = "Pixel 2"
        apiLevel = 35
        systemImageSource = "aosp"
    }
}
