package shared.xmpp.halcyon

import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import org.koin.core.annotation.Single
import shared.xmpp.api.XmppConnectionService
import tigase.halcyon.core.AbstractHalcyon
import tigase.halcyon.core.Halcyon
import tigase.halcyon.core.HalcyonStateChangeEvent
import tigase.halcyon.core.builder.createHalcyon
import tigase.halcyon.core.connector.ReceivedXMLElementEvent
import tigase.halcyon.core.connector.SentXMLElementEvent
import tigase.halcyon.core.xmpp.BareJID
import tigase.halcyon.core.xmpp.createBareJID

@Single
internal class XmppConnectionServiceHalcyon(
    private val connectorInitializer: ConnectorInitializer
) : XmppConnectionService {

    var halcyon: Halcyon? = null
    var bareJID: BareJID? = null
    var state: AbstractHalcyon.State? = null

    override suspend fun isConnected(): Boolean = halcyon?.running == true

    override suspend fun connect(username: String, password: String, domain: String) {
        bareJID = createBareJID(username, domain)
        halcyon?.disconnect()
        halcyon = createHalcyon {
            auth {
                userJID = bareJID
                password { password }
            }
            connectorInitializer.init(this)
        }.also {
            it.eventBus.register(HalcyonStateChangeEvent) { event ->
                println("Halcyon:Event: StateChange: ${event.oldState}->${event.newState}")
                state = event.newState
            }
            it.eventBus.register(SentXMLElementEvent) { event ->
                println("Halcyon:Event: XML>>> ${event.element.getAsString()}")
            }
            it.eventBus.register(ReceivedXMLElementEvent) { event ->
                println("Halcyon:Event: XML<<< ${event.element.getAsString()}")
            }
        }
        halcyon?.connect()
        while (state != AbstractHalcyon.State.Connected) {
            delay(100.milliseconds)
        }
    }

    override suspend fun disconnect() {
        halcyon?.disconnect()
        bareJID = null
        state = null
    }
}
