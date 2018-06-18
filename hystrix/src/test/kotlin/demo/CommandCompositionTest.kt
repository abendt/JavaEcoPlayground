package demo

import kotlinx.coroutines.experimental.rx1.awaitFirst
import kotlinx.coroutines.experimental.rx1.rxSingle
import org.junit.Test
import kotlin.system.measureTimeMillis

class CommandCompositionTest {
    @Test
    fun hystrixCompose2() {
        val time = measureTimeMillis {
            val t1 = getUserName().toObservable()
            val t2 = getUserName().observe()
            Thread.sleep(1000)

            println(t1.toBlocking().first())
            println(t2.toBlocking().first())
        }

        println("Time " + time)
    }

    @Test
    fun hystrixCompose() {
        val time = measureTimeMillis {
            val name = getUserName().observe()
                    .flatMap { sayHelloTo(it).observe() }

            val result = name.zipWith(getMessageOfTheDay().observe()) { l, r -> "$l $r" }

            println(result.toBlocking().first())
        }

        println("Time " + time)
    }

    @Test
    fun coRoutineCompose() {
        val time = measureTimeMillis {

            val single = rxSingle {
                val name = getUserName().observe()
                val greeting = sayHelloTo(name.awaitFirst()).observe()
                val motd = getMessageOfTheDay().observe()

                "${greeting.awaitFirst()} ${motd.awaitFirst()}"
            }

            println(single.toBlocking().value())
        }

        println("Time " + time)
    }

    fun getUserName() = hystrixCommand("NAME") {
        Thread.sleep(1000)
        "Kotlin"
    }

    fun sayHelloTo(name: String) = hystrixCommand("HELLO") {
        Thread.sleep(1000)
        "Hello $name"
    }

    fun getMessageOfTheDay() = hystrixCommand("MOTD") {
        Thread.sleep(1000)
        "have a nice day"
    }
}