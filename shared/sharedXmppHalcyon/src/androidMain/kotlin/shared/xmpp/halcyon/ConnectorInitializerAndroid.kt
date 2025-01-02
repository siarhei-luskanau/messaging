package shared.xmpp.halcyon

import org.koin.core.annotation.Single
import shared.xmpp.api.XmppConstants.XMPP_SERVER_HOSTNAME
import shared.xmpp.api.XmppConstants.XMPP_SERVER_PORT
import tigase.halcyon.core.builder.ConfigurationBuilder
import tigase.halcyon.core.builder.socketConnector

@Single
internal class ConnectorInitializerAndroid : ConnectorInitializer {

    override fun init(configurationBuilder: ConfigurationBuilder) {
        configurationBuilder.apply {
            socketConnector {
                hostname = XMPP_SERVER_HOSTNAME
                port = XMPP_SERVER_PORT
            }
        }
    }
}
