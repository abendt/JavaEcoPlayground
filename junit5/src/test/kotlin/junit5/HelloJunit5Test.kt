package junit5

import org.assertj.core.api.Assumptions.assumeThat
import org.assertj.core.api.KotlinAssertions
import org.junit.jupiter.api.Test

class HelloJunit5Test {
    @Test
    fun helloJunit() {
        KotlinAssertions.assertThat("hello world").isEqualTo("hello world")
    }

    @Test
    fun aFailingTest() {
        assumeThat(false).isTrue()
    }
}