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
import org.koin.core.annotation.Single
import shared.xmpp.api.XmppConnectionService
import shared.xmpp.api.XmppConstants.XMPP_SERVER_HOSTNAME
import shared.xmpp.api.XmppConstants.XMPP_SERVER_PORT

@Single
internal class XmppConnectionServiceSmack : XmppConnectionService {

    var connection: AbstractXMPPConnection? = null

    override suspend fun isConnected(): Boolean = connection?.isConnected == true

    override suspend fun connect(username: String, password: String, domain: String) {
        connection?.disconnect()

        SASLAuthentication.registerSASLMechanism(SASLPlainMechanism())
        val config: XMPPTCPConnectionConfiguration = XMPPTCPConnectionConfiguration.builder()
            .setXmppDomain(domain)
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

        connection = XMPPTCPConnection(config).also {
            it.connect()
            it.login(username, password, Resourcepart.fromOrNull("Android"))
        }
    }

    override suspend fun disconnect() {
        connection?.disconnect()
        connection?.instantShutdown()
    }
}
