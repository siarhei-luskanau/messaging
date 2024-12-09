package shared.xmpp.halcyon

import shared.xmpp.api.XmppService
import tigase.halcyon.core.Halcyon
import tigase.halcyon.core.HalcyonStateChangeEvent
import tigase.halcyon.core.builder.createHalcyon
import tigase.halcyon.core.xmpp.toBareJID

class XmppServiceHalcyon(private val connectorInitializer: ConnectorInitializer) : XmppService {

    var halcyon: Halcyon? = null

    override fun connect(username: String, password: String) {
        halcyon?.disconnect()
        halcyon = createHalcyon {
            auth {
                userJID = username.toBareJID()
                password { password }
                authenticationName = connectorInitializer.getAuthenticationName()
            }
            connectorInitializer.init(this)
        }.also {
            it.eventBus.register(HalcyonStateChangeEvent) { stateChangeEvent ->
                println("Halcyon state: ${stateChangeEvent.oldState}->${stateChangeEvent.newState}")
            }
            it.connect()
        }
    }

    override fun disconnect() {
        halcyon?.disconnect()
    }
}
