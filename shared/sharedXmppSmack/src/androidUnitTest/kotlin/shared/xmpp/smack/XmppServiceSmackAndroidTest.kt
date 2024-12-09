package shared.xmpp.smack

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class XmppServiceSmackAndroidTest {

    @Test
    fun connectTest() {
        runTest {
            val xmppServiceSmack = XmppServiceSmack(
                xmppHost = XMPP_SERVER_HOSTNAME,
                xmppPort = XMPP_SERVER_PORT,
                xmppDomain = XMPP_DOMAIN
            )
            xmppServiceSmack.connect(username = XMPP_USER, password = XMPP_PASSWORD)

            assertTrue(
                actual = requireNotNull(xmppServiceSmack.connection).isConnected,
                message = "connection.isConnected should be true"
            )
            assertTrue(
                actual = requireNotNull(xmppServiceSmack.connection).isAuthenticated,
                message = "connection.isAuthenticated should be true"
            )

            xmppServiceSmack.disconnect()
            assertFalse(
                actual = requireNotNull(xmppServiceSmack.connection).isAuthenticated,
                message = "connection.isAuthenticated should be false"
            )
            assertFalse(
                actual = requireNotNull(xmppServiceSmack.connection).isConnected,
                message = "connection.isConnected should be false"
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
