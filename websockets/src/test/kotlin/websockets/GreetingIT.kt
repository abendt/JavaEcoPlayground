package websockets

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.assertj.core.api.KotlinAssertions
import org.awaitility.Awaitility
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
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

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingIntegrationTests {

    val logger = KotlinLogging.logger {}

    @LocalServerPort
    private val port: Int = 0

    lateinit var stompClient: WebSocketStompClient

    private val headers = WebSocketHttpHeaders(HttpHeaders().apply {
        add(HttpHeaders.COOKIE, "my cookie")
    })

    @get:Rule
    var collector = ErrorCollector()

    @Before
    fun setup() {
        val transports = listOf(
                WebSocketTransport(StandardWebSocketClient()))

        val sockJsClient = SockJsClient(transports)

        stompClient = WebSocketStompClient(sockJsClient).apply {
            messageConverter = MappingJackson2MessageConverter().apply {
                setPrettyPrint(true)
                objectMapper = jacksonObjectMapper()
            }
        }
    }

    private fun createHandler(latch: CountDownLatch): TestSessionHandler {
        return object : TestSessionHandler(collector) {

            override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders?) {

                logger.info { "connected ${session.isConnected} $connectedHeaders" }

                val subscriptions = mutableListOf<StompSession.Subscription>()

                val reply = session.subscribe("/user/queue/reply", object : StompFrameHandler {
                    override fun getPayloadType(headers: StompHeaders): Type {
                        return Greeting::class.java
                    }

                    override fun handleFrame(headers: StompHeaders, payload: Any) {
                        println("XXX: " + payload)
                    }
                })

                subscriptions.add(reply)

                val greetings = session.subscribe("/topic/greetings", object : StompFrameHandler {
                    override fun getPayloadType(headers: StompHeaders): Type {
                        return Greeting::class.java
                    }

                    override fun handleFrame(headers: StompHeaders, payload: Any) {
                        val (content) = payload as Greeting

                        try {
                            collector.checkThat(content, Matchers.equalTo("Hello, Spring!"))
                        } finally {
                            subscriptions.forEach {
                                it.unsubscribe()
                            }

                            session.disconnect()
                            latch.countDown()
                        }
                    }
                })

                subscriptions.add(greetings)

                try {
                    session.send("/app/hello", HelloMessage("Spring"))

                    logger.info { "Sent hello!" }
                } catch (t: Throwable) {
                    collector.addError(t)
                    latch.countDown()
                }
            }
        }
    }

    @Test
    fun getGreeting() {

        val latch = CountDownLatch(1)

        val handler = createHandler(latch)

        stompClient.connect("ws://localhost:{port}/gs-guide-websocket", headers, handler, port)

        val result = latch.await(30, TimeUnit.SECONDS)

        KotlinAssertions.assertThat(result).isTrue()
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