package shared.xmpp.smack

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
import shared.xmpp.api.XmppService

class XmppServiceSmack(
    private val xmppHost: String,
    private val xmppPort: Int,
    private val xmppDomain: String
) : XmppService {

    var connection: AbstractXMPPConnection? = null

    override fun connect(username: String, password: String) {
        connection?.disconnect()

        SASLAuthentication.registerSASLMechanism(SASLPlainMechanism())
        val config: XMPPTCPConnectionConfiguration = XMPPTCPConnectionConfiguration.builder()
            .setXmppDomain(xmppDomain)
            .setHost(xmppHost)
            .setPort(xmppPort)
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

        connection = XMPPTCPConnection(config).also {
            it.connect()
            it.login(username, password, Resourcepart.fromOrNull("Android"))
        }
    }

    override fun disconnect() {
        connection?.disconnect()
        connection?.instantShutdown()
    }
}
