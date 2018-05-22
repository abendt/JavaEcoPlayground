package junit5

import org.assertj.core.api.KotlinAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class HelloJunit5Test {
    @Test
    fun helloJunit() {
        KotlinAssertions.assertThat("hello world").isEqualTo("hello world")
    }

    @Test
    fun aFailingTest() {
        fail {
           "bla"
        }
    }
}