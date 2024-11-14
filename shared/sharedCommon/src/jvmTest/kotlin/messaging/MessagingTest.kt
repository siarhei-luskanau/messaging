package messaging

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.debugger.SmackDebugger
import org.jivesoftware.smack.packet.TopLevelStreamElement
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smack.util.TLSUtils.PROTO_TLSV1_2
import org.jxmpp.jid.EntityFullJid
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
    @Ignore("smackTest")
    fun smackTest() {
        val config: XMPPTCPConnectionConfiguration = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(USER, PASSWORD)
            .setXmppDomain("localhost")
            .setResource("Android_Client")
            .setHost("localhost")
            .setPort(5222)
            .setEnabledSSLProtocols(arrayOf(PROTO_TLSV1_2))
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .setHostnameVerifier { _, _ -> true }
            .setDnssecMode(ConnectionConfiguration.DnssecMode.disabled)
            .setDebuggerFactory { connection ->
                object : SmackDebugger(connection) {
                    override fun userHasLogged(user: EntityFullJid?) {
                        println("userHasLogged: $user")
                    }
                    override fun outgoingStreamSink(outgoingCharSequence: CharSequence?) {
                        println("outgoingStreamSink: $outgoingCharSequence")
                    }
                    override fun incomingStreamSink(incomingCharSequence: CharSequence?) {
                        println("incomingStreamSink: $incomingCharSequence")
                    }
                    override fun onIncomingStreamElement(streamElement: TopLevelStreamElement?) {
                        println("onIncomingStreamElement: $streamElement")
                    }
                    override fun onOutgoingStreamElement(streamElement: TopLevelStreamElement?) {
                        println("onOutgoingStreamElement: $streamElement")
                    }
                }
            }
//            .setSslContextFactory {
//                SSLContext
//            }
            .build()

        val connection: AbstractXMPPConnection = XMPPTCPConnection(config)
        connection.connect()
        connection.login()
    }

    @Test
    @Ignore("tigaseHalcyonTest")
    fun tigaseHalcyonTest() {
        val user = USER
        val password = PASSWORD
        val halcyon = createHalcyon {
            socketConnector {
                hostname = "localhost"
                port = 5222
            }
            auth {
                userJID = user.toBareJID()
                password { password }
            }
        }
        halcyon.connectAndWait()

        halcyon.getModule(PingModule)
            .ping("localhost".toJID())
            .response { result ->
                result.onSuccess { pong -> println("Pong: ${pong.time}ms") }
                result.onFailure { error -> println("Error $error") }
            }
            .send()

        halcyon.waitForAllResponses()
        halcyon.disconnect()
    }

    companion object {
        private const val USER = "admin"
        private const val PASSWORD = "passw0rd"
    }
}
