package http4k

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.github.resilience4j.bulkhead.Bulkhead
import org.awaitility.Awaitility
import org.http4k.client.JavaHttpClient
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.filter.DebuggingFilters
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.concurrent.atomic.AtomicReference
import io.github.resilience4j.bulkhead.BulkheadConfig
import org.http4k.client.AsyncHttpClient
import org.http4k.filter.ResilienceFilters

class HelloHttp4kTest {

    @get:Rule
    val wireMockRule = WireMockRule(WireMockConfiguration.options().dynamicPort())

    @Test
    fun simpleGet() {
        givenValidResponse()

        val request = Request(Method.GET, "http://localhost:${wireMockRule.port()}/hello")
        val response = JavaHttpClient()(request)

        expectThat(response.status).isEqualTo(Status.OK)
        expectThat(response.bodyString()).isEqualTo("Hello world!")
    }

    @Test
    fun canLogRequest() {
        givenValidResponse()

        val enhanceHandler = DebuggingFilters.PrintRequestAndResponse

        val request = Request(Method.GET, "http://localhost:${wireMockRule.port()}/hello")

        val client = JavaHttpClient()

        val handler = enhanceHandler(System.out)(client)

        handler(request)
    }

    @Test
    fun asyncOkHttp() {
        givenValidResponse()

        val request = Request(Method.GET, "http://localhost:${wireMockRule.port()}/hello")

        val handler = OkHttp()

        val result = AtomicReference<String>()

        handler(request) {
            expectThat(it.status).isEqualTo(Status.OK)
            result.set(it.bodyString())
        }

        Awaitility.await().until {
            result.get() == "Hello world!"
        }
    }

    @Test
    fun canUseResilience4j() {

        // AsyncHttpClient ist kein Handler und kann damit nicht dekoriert werden!

//        wireMockRule.stubFor(WireMock.get(WireMock.urlEqualTo("/hello"))
//                .willReturn(WireMock.aResponse()
//                        .withFixedDelay(100)
//                        .withStatus(200)
//                        .withBody("Hello world!")))
//
//        val request = Request(Method.GET, "http://localhost:${wireMockRule.port()}/hello")
//
//        val bulkheadConfig = BulkheadConfig.custom()
//                .maxConcurrentCalls(5)
//                .maxWaitTime(0)
//                .build()
//
//        val enhance = ResilienceFilters.Bulkheading(Bulkhead.of("bulkhead", bulkheadConfig))
//
//        val client: AsyncHttpClient = OkHttp()
//
//        val handler = enhance(client)
//
//        val result = mutableListOf<Status>()
//
//        (1..10).forEach {
//            handler(request) { response ->
//                println("response " + response.status)
//                result.add(response.status)
//            }
//        }
//
//        Awaitility.await().until {
//            result.size == 10
//        }
    }

    private fun givenValidResponse() {
        wireMockRule.stubFor(WireMock.get(WireMock.urlEqualTo("/hello"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody("Hello world!")))
    }
}