package shared.xmpp.api

interface XmppConnectionService {
    suspend fun isConnected(): Boolean
    suspend fun connect(username: String, password: String, domain: String)
    suspend fun disconnect()
}
