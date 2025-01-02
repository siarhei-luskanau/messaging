package shared.xmpp.halcyon

import org.koin.core.annotation.Single
import shared.xmpp.api.XMPP_SERVER_ADDRESS
import shared.xmpp.api.XMPP_SERVER_PORT
import tigase.halcyon.core.builder.ConfigurationBuilder
import tigase.halcyon.core.builder.socketConnector

@Single
internal class ConnectorInitializerAndroid : ConnectorInitializer {

    override fun init(configurationBuilder: ConfigurationBuilder) {
        configurationBuilder.apply {
            socketConnector {
                hostname = XMPP_SERVER_ADDRESS
                port = XMPP_SERVER_PORT
            }
        }
    }
}
