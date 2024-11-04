package messaging

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import rocks.xmpp.core.net.client.TcpConnectionConfiguration
import rocks.xmpp.core.session.XmppClient
import tigase.halcyon.core.builder.createHalcyon
import tigase.halcyon.core.builder.socketConnector
import tigase.halcyon.core.xmpp.modules.PingModule
import tigase.halcyon.core.xmpp.toBareJID
import tigase.halcyon.core.xmpp.toJID

class MessagingTest {

    @Test
    fun sumTest() {
        assertEquals(actual = 2 + 2, expected = 4)
    }

    @Test
    @Ignore("tigase.halcyon.core.connector.ConnectorException: Unexpected stop!")
    fun halcyonTest() {
        val user = "admin"
        val password = "passw0rd"
        val halcyon = createHalcyon {
            socketConnector {
                hostname = "127.0.0.1"
                port = 5222
            }
            auth {
                userJID = user.toBareJID()
                password { password }
            }
        }
        halcyon.connectAndWait()

        halcyon.getModule(PingModule)
            .ping("tigase.org".toJID())
            .response { result ->
                result.onSuccess { pong -> println("Pong: ${pong.time}ms") }
                result.onFailure { error -> println("Error $error") }
            }
            .send()

        halcyon.waitForAllResponses()
        halcyon.disconnect()
    }

    @Test
    @Ignore(
        "javax.net.ssl.SSLHandshakeException: PKIX path building failed: " +
            "sun.security.provider.certpath.SunCertPathBuilderException: " +
            "unable to find valid certification path to requested target"
    )
    fun rocksXmppTest() {
        val tcpConfiguration = TcpConnectionConfiguration.builder()
            .hostname("127.0.0.1")
            .port(5222)
            .build()
        val xmppClient = XmppClient.create("localhost", tcpConfiguration)
        xmppClient.connect()
        xmppClient.loginAnonymously()
    }
}
