package shared.xmpp

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.ksp.generated.module
import org.robolectric.RobolectricTestRunner
import shared.xmpp.api.PingXmppService
import shared.xmpp.api.XmppConnectionService
import shared.xmpp.api.XmppConstants.XMPP_DOMAIN
import shared.xmpp.api.XmppConstants.XMPP_PASSWORD
import shared.xmpp.api.XmppConstants.XMPP_USER

@RunWith(RobolectricTestRunner::class)
class PingXmppServiceTest {

    private val koin by lazy { startKoin { modules(XmppModule().module) } }

    private val xmppConnectionService: XmppConnectionService by lazy { koin.koin.get() }
    private val pingXmppService: PingXmppService by lazy { koin.koin.get() }

    @BeforeTest
    fun beforeTest() {
        koin.koin
    }

    @AfterTest
    fun afterTest() {
        stopKoin()
    }

    @Test
    fun pingTest() {
        runTest {
            xmppConnectionService.connect(
                username = XMPP_USER,
                password = XMPP_PASSWORD,
                domain = XMPP_DOMAIN
            )
            assertTrue(
                actual = pingXmppService.ping(),
                message = "ping should be true"
            )
        }
    }
}
