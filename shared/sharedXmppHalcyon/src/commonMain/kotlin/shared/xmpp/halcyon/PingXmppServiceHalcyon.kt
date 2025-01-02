package shared.xmpp.halcyon

import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import shared.xmpp.api.PingXmppService
import tigase.halcyon.core.ReflectionModuleManager
import tigase.halcyon.core.xmpp.modules.PingModule

@Single
internal class PingXmppServiceHalcyon(
    private val xmppConnectionServiceHalcyon: XmppConnectionServiceHalcyon
) : PingXmppService {

    @OptIn(ReflectionModuleManager::class)
    override suspend fun ping(): Boolean = suspendCancellableCoroutine { continuation ->
        requireNotNull(xmppConnectionServiceHalcyon.halcyon).getModule<PingModule>()
            .ping()
            .response { response ->
                response.onSuccess { successResult ->
                    continuation.resumeWith(Result.success(successResult.time.isPositive()))
                }
                response.onFailure { error ->
                    continuation.resumeWith(Result.failure(error))
                }
            }.send()
    }
}
