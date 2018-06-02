package websockets

import com.sun.security.auth.UserPrincipal
import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.server.HandshakeHandler
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : AbstractWebSocketMessageBrokerConfigurer() {

    val LOG = KotlinLogging.logger {}

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/gs-guide-websocket").setHandshakeHandler(handshakeHandler())
                .withSockJS()
    }

    private fun handshakeHandler(): HandshakeHandler {
        return object : DefaultHandshakeHandler() {
            override fun determineUser(
                    request: ServerHttpRequest,
                    wsHandler: WebSocketHandler,
                    attributes: MutableMap<String, Any>
            ): Principal {
                if (request is ServletServerHttpRequest) {
                    val servletRequest = request.servletRequest
                    val cookie = servletRequest.getHeader(HttpHeaders.COOKIE)

                    attributes["token"] = cookie
                }

                return UserPrincipal("myuser")
            }
        }
    }

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic", "/queue")
        config.setApplicationDestinationPrefixes("/app")
    }

}