package shared.xmpp.halcyon

import tigase.halcyon.core.builder.ConfigurationBuilder
import tigase.halcyon.core.builder.socketConnector

class ConnectorInitializerIos(
    private val xmppHost: String,
    private val xmppPort: Int,
    private val xmppDomain: String
) : ConnectorInitializer {

    override fun init(configurationBuilder: ConfigurationBuilder) {
        configurationBuilder.apply {
            socketConnector {
                hostname = xmppHost
                port = xmppPort
            }
        }
    }

    override fun getAuthenticationName(): String = xmppDomain
}
