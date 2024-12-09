package shared.xmpp.halcyon

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import tigase.halcyon.core.xmpp.toJID

class XmppServiceHalcyonIosTest {

    @Test
    fun connectTest() {
        runTest {
            val xmppService = XmppServiceHalcyon(
                connectorInitializer = ConnectorInitializerIos(
                    xmppHost = XMPP_SERVER_HOSTNAME,
                    xmppPort = XMPP_SERVER_PORT,
                    xmppDomain = XMPP_DOMAIN
                )
            )
            xmppService.connect(username = XMPP_USER, password = XMPP_PASSWORD)
            assertTrue(
                actual = requireNotNull(xmppService.halcyon).running,
                message = "halcyon.running should be true"
            )

            requireNotNull(xmppService.halcyon).request.message {
                to = "user2".toJID()
                "body" {
                    +"Art thou not Romeo, and a Montague?"
                }
            }.send()

            requireNotNull(xmppService.halcyon).disconnect()
            assertFalse(
                actual = requireNotNull(xmppService.halcyon).running,
                message = "halcyon.running should be false"
            )
        }
    }

    companion object {
        private const val XMPP_USER = "user1"
        private const val XMPP_PASSWORD = "user1password"
        private const val XMPP_SERVER_HOSTNAME = "127.0.0.1"
        private const val XMPP_SERVER_PORT = 5222
        private const val XMPP_DOMAIN = "localhost"
    }
}
