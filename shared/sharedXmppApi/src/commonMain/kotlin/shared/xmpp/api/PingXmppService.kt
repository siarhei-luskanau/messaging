package shared.xmpp.api

interface PingXmppService {
    suspend fun ping(): Boolean
}
