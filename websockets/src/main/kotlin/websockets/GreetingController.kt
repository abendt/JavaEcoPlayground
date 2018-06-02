package websockets

import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import org.springframework.web.util.HtmlUtils

@Controller
class GreetingController(val messagingTemplate: SimpMessagingTemplate) {

    val logger = KotlinLogging.logger {}

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    fun greeting(message: HelloMessage): Greeting {
        logger.info { "greeting $message" }

        messagingTemplate.convertAndSendToUser("myuser", "/queue/reply", Greeting("Welcome ${message.name}"))

        Thread.sleep(1000) // simulated delay
        return Greeting("Hello, " + HtmlUtils.htmlEscape(message.name) + "!")
    }


    @EventListener
    fun onSessionConnect(event: SessionConnectedEvent) {
        println("Session connected")
    }

    @EventListener
    fun onSubscribe(event: SessionSubscribeEvent) {
        println("Session subscribed " + event.message)
    }
}