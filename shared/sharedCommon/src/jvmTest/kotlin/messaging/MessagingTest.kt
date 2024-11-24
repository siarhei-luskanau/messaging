package messaging

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.SASLAuthentication
import org.jivesoftware.smack.debugger.SmackDebugger
import org.jivesoftware.smack.packet.TopLevelStreamElement
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.parts.Resourcepart
import tigase.halcyon.core.HalcyonStateChangeEvent
import tigase.halcyon.core.builder.createHalcyon
import tigase.halcyon.core.builder.socketConnector
import tigase.halcyon.core.xmpp.toBareJID

class MessagingTest {

    @Test
    fun smackTest() {
        SASLAuthentication.registerSASLMechanism(SASLPlainMechanism())
        val config: XMPPTCPConnectionConfiguration = XMPPTCPConnectionConfiguration.builder()
            .setXmppDomain(XMPP_DOMAIN)
            .setHost(XMPP_SERVER_HOSTNAME)
            .setPort(XMPP_SERVER_PORT)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .setHostnameVerifier { _, _ -> true }
            .setDebuggerFactory { connection ->
                object : SmackDebugger(connection) {
                    override fun userHasLogged(user: EntityFullJid?) {
                        println("SmackDebugger:userHasLogged: $user")
                    }
                    override fun outgoingStreamSink(outgoingCharSequence: CharSequence?) {
                        println("SmackDebugger:outgoingStreamSink:>>>: $outgoingCharSequence")
                    }
                    override fun incomingStreamSink(incomingCharSequence: CharSequence?) {
                        println("SmackDebugger:incomingStreamSink:<<<: $incomingCharSequence")
                    }
                    override fun onIncomingStreamElement(streamElement: TopLevelStreamElement?) {
                        println("SmackDebugger:onIncomingStreamElement: $streamElement")
                    }
                    override fun onOutgoingStreamElement(streamElement: TopLevelStreamElement?) {
                        println("SmackDebugger:onOutgoingStreamElement: $streamElement")
                    }
                }
            }
            .build()

        val connection: AbstractXMPPConnection = XMPPTCPConnection(config)

        connection.connect()
        assertTrue(actual = connection.isConnected, message = "connection.isConnected")

        connection.login(XMPP_USER, XMPP_PASSWORD, Resourcepart.fromOrNull("TestClient"))
        assertTrue(actual = connection.isAuthenticated, message = "connection.isAuthenticated")

        connection.disconnect()
        assertFalse(actual = connection.isAuthenticated, message = "connection.isAuthenticated")
        assertFalse(actual = connection.isConnected, message = "connection.isConnected")

        connection.instantShutdown()
    }

    @Test
    @Ignore("tigaseHalcyonTest")
    fun tigaseHalcyonTest() {
        val halcyon = createHalcyon {
            auth {
                userJID = XMPP_USER.toBareJID()
                password { XMPP_PASSWORD }
            }
            socketConnector {
                hostname = XMPP_SERVER_HOSTNAME
                port = XMPP_SERVER_PORT
            }
        }
        halcyon.eventBus.register(HalcyonStateChangeEvent) { stateChangeEvent ->
            println("Halcyon state: ${stateChangeEvent.oldState}->${stateChangeEvent.newState}")
        }
        halcyon.connectAndWait()

//        halcyon.getModule(PingModule)
//            .ping(XMPP_DOMAIN.toJID())
//            .response { result ->
//                result.onSuccess { pong -> println("Pong: ${pong.time}ms") }
//                result.onFailure { error -> println("Error $error") }
//            }
//            .send()

//        halcyon.waitForAllResponses()
//        halcyon.disconnect()
    }

    companion object {
        private const val XMPP_USER = "user1"
        private const val XMPP_PASSWORD = "user1password"
        private const val XMPP_DOMAIN = "localhost"
        private const val XMPP_SERVER_HOSTNAME = "127.0.0.1"
        private const val XMPP_SERVER_PORT = 5222
    }
}
