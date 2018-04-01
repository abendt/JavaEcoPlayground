package json

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.KotlinAssertions
import org.junit.Test

class InheritanceMappingTest {

    val mapper = jacksonObjectMapper()

    @Test
    fun serializeToJson() {

        println(mapper.writeValueAsString(UserCreatedEvent("kotlin")))
        println(mapper.writeValueAsString(UserActiveEvent("kotlin", "click1")))
        println(mapper.writeValueAsString(UserActiveEvent("kotlin", "click2")))
    }

    @Test
    fun deserializeFromJson() {
        val asEvent = mapper.readValue("""{"@type":"userCreated","source":"kotlin"}""", EventBase::class.java)

        KotlinAssertions.assertThat(asEvent).isInstanceOf(UserCreatedEvent::class.java)

        val userActiveEvent = mapper.readValue("""{"@type":"userActive","source":"kotlin","action":"click1"}""", UserActiveEvent::class.java)

        KotlinAssertions.assertThat(userActiveEvent.source).isEqualTo("kotlin")
        KotlinAssertions.assertThat(userActiveEvent.action).isEqualTo("click1")
    }

    @Test
    fun serializeEvent1() {
        println(mapper.writeValueAsString(Event1("kotlin")))
    }

    @Test
    fun  deserializeEvent1() {
        val result = mapper.readValue("""{"@type":"event1","source":"kotlin"}""", CustomEventBase::class.java)
    }
}