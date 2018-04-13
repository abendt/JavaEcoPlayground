package demo

import com.netflix.hystrix.exception.HystrixRuntimeException
import org.assertj.core.api.KotlinAssertions
import org.junit.Test
import kotlin.test.assertFailsWith

class WithHystrixTest {

    @Test
    fun canExecuteCommand() {
        val result = withHystrix("HELLO") {
            "Hello Kotlin"
        }

        KotlinAssertions.assertThat(result).isEqualTo("Hello Kotlin")
    }

    @Test
    fun commandException() {
        assertFailsWith<HystrixRuntimeException> {
            withHystrix("HELLO") {
                throw RuntimeException()
            }
        }
    }

    @Test
    fun commandFallback() {
        val result = withHystrix("HELLO", fallback = { "Hello Fallback" }, command = {
            throw RuntimeException()
        })

        KotlinAssertions.assertThat(result).isEqualTo("Hello Fallback")
    }

    @Test
    fun commandTimeout() {
        assertFailsWith<HystrixRuntimeException> {
            withHystrix("HELLO", timeout = 50) {
                Thread.sleep(2000)
            }
        }
    }
}