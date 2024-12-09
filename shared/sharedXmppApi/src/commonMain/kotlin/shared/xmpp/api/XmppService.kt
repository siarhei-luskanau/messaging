package shared.xmpp.api

interface XmppService {
    fun connect(username: String, password: String)
    fun disconnect()
}
