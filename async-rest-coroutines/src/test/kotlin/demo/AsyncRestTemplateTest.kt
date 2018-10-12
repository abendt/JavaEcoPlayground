package demo

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.future.future
import mu.KotlinLogging
import org.awaitility.Awaitility
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.springframework.http.ResponseEntity
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.client.AsyncRestTemplate
import java.util.concurrent.CompletableFuture

class AsyncRestTemplateTest {

    val logger = KotlinLogging.logger {}

    private val asyncRestTemplate = AsyncRestTemplate()

    @get:Rule
    val wireMockRule = WireMockRule(WireMockConfiguration.options().dynamicPort())

    @get:Rule
    var collector = ErrorCollector()

    @Test
    fun plainAsyncRestTemplate() {
        givenValidResponse()

        val future = springAsyncRequest()

        Awaitility.await().until { future.get().body == "Hello world!" }
    }

    private fun springAsyncRequest(): ListenableFuture<ResponseEntity<String>> {
        logger.info { "start async request" }
        return asyncRestTemplate.getForEntity("http://localhost:${wireMockRule.port()}/slow", String::class.java)
    }

    @Test
    fun wrapInCompletableFuture() {
        givenValidResponse()

        val future = completableFutureRequest()

        Awaitility.await().until { future.get() == "Hello world!" }
    }

    private fun completableFutureRequest(): CompletableFuture<String> {
        val future = springAsyncRequest()

        val completableFuture = CompletableFuture<String>()

        future.addCallback({ result ->
            logger.info { "request completed" }
            completableFuture.complete(result.body)
        },
                { ex ->
                    logger.info { "request failed" }
                    completableFuture.completeExceptionally(ex)
                })
        return completableFuture
    }

    @Test
    fun wrapInCouroutine() {
        givenValidResponse()

        val results = loadTimes(1, {
            completableFutureRequest()
        })

        Awaitility.await().until { results.get() == 1 }
    }

    @Test
    fun errorHandling() {
        givenErrorResponse()

        val result = loadTimes(1, {
            completableFutureRequest()
        })

        Awaitility.await().until { result.isCompletedExceptionally }
    }

    private fun givenValidResponse() {
        wireMockRule.stubFor(get(urlEqualTo("/slow"))
                .willReturn(aResponse()
                        .withFixedDelay(1000)
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("Hello world!")))
    }

    private fun givenErrorResponse() {
        wireMockRule.stubFor(get(urlEqualTo("/slow"))
                .willReturn(aResponse()
                        .withFixedDelay(1000)
                        .withStatus(500)
                ))
    }

    private fun <T> loadTimes(count: Int, action: () -> CompletableFuture<T>) = future {
        val loaders = (1..count).map {
            action()
        }

        loaders.forEach { it.await() }

        loaders.size
    }
}