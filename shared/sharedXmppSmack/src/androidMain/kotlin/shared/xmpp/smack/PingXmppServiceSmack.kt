package shared.xmpp.smack

import org.jivesoftware.smackx.ping.PingManager
import org.koin.core.annotation.Single
import shared.xmpp.api.PingXmppService

@Single
internal class PingXmppServiceSmack(
    private val xmppConnectionServiceSmack: XmppConnectionServiceSmack
) : PingXmppService {

    override suspend fun ping(): Boolean =
        PingManager.getInstanceFor(requireNotNull(xmppConnectionServiceSmack.connection))
            .pingMyServer()
}
