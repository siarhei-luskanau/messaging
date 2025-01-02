package shared.xmpp.halcyon

import tigase.halcyon.core.builder.ConfigurationBuilder

interface ConnectorInitializer {
    fun init(configurationBuilder: ConfigurationBuilder)
}
