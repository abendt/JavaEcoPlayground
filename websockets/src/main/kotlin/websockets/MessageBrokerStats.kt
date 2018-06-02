package websockets

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.socket.config.WebSocketMessageBrokerStats

@Controller
class MessageBrokerStats(val stats: WebSocketMessageBrokerStats) {

    @RequestMapping("/stats")
    @ResponseBody
    fun showStats() = stats

}