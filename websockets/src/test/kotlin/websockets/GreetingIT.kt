package websockets

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.awaitility.Awaitility
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.fail

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingIntegrationTests {

    val logger = KotlinLogging.logger {}

    @LocalServerPort
    private val port: Int = 0

    lateinit var sockJsClient: SockJsClient

    lateinit var stompClient: WebSocketStompClient

    private val headers = WebSocketHttpHeaders(HttpHeaders().apply {
        add(HttpHeaders.COOKIE, "my cookie")
    })

    @get:Rule
    var collector = ErrorCollector()

    @Before
    fun setup() {
        val transports = ArrayList<Transport>().apply {
            add(WebSocketTransport(StandardWebSocketClient()))
        }
        this.sockJsClient = SockJsClient(transports)

        this.stompClient = WebSocketStompClient(sockJsClient)
        this.stompClient.messageConverter = MappingJackson2MessageConverter().apply {
            setPrettyPrint(true)
            objectMapper = jacksonObjectMapper()
        }
    }

    @Test
    fun getGreeting() {

        val latch = CountDownLatch(1)

        val handler = object : TestSessionHandler(collector) {

            override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders?) {

                logger.info { "connected ${session.isConnected} $connectedHeaders" }

                session.subscribe("/user/queue/reply", object: StompFrameHandler {
                    override fun getPayloadType(headers: StompHeaders): Type {
                        return Greeting::class.java
                    }

                    override fun handleFrame(headers: StompHeaders, payload: Any) {
                        println("XXX: " + payload)
                    }
                })

                session.subscribe("/topic/greetings", object : StompFrameHandler {
                    override fun getPayloadType(headers: StompHeaders): Type {
                        return Greeting::class.java
                    }

                    override fun handleFrame(headers: StompHeaders, payload: Any) {
                        val (content) = payload as Greeting

                        try {
                            collector.checkThat(content, Matchers.equalTo("Hello, Spring!"))
                        } finally {
                            session.disconnect()
                            latch.countDown()
                        }
                    }
                })

                try {
                    session.send("/app/hello", HelloMessage("Spring"))

                    logger.info { "Sent hello!" }
                } catch (t: Throwable) {
                    collector.addError(t)
                    latch.countDown()
                }
            }
        }

        stompClient.connect("ws://localhost:{port}/gs-guide-websocket", this.headers, handler, this.port)

        Awaitility.await().until { latch.await(1, TimeUnit.SECONDS) }
    }

    private open inner class TestSessionHandler(private val failure: ErrorCollector) : StompSessionHandlerAdapter() {

        override fun handleFrame(headers: StompHeaders?, payload: Any?) {
            failure.addError(Exception(headers!!.toString()))
        }

        override fun handleException(s: StompSession?, c: StompCommand?, h: StompHeaders?, p: ByteArray?, ex: Throwable?) {
            failure.addError(ex)
        }

        override fun handleTransportError(session: StompSession?, ex: Throwable?) {
            failure.addError(ex)
        }
    }
}