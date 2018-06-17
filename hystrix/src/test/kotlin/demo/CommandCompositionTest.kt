package demo

import kotlinx.coroutines.experimental.rx1.awaitFirst
import kotlinx.coroutines.experimental.rx1.rxSingle
import org.junit.Test
import kotlin.system.measureTimeMillis

class CommandCompositionTest {
    @Test
    fun hystrixCompose2() {
        val time = measureTimeMillis {
            val t1 = command1().toObservable()
            val t2 = command1().observe()
            Thread.sleep(1000)

            println(t1.toBlocking().first())
            println(t2.toBlocking().first())

        }

        println("Time " + time)
    }

    @Test
    fun hystrixCompose() {
        val time = measureTimeMillis {
            val name = command1().observe()
                    .flatMap { command2(it).observe() }

            val result = name.zipWith(command3().observe()) { l, r -> "$l $r" }

            println(result.toBlocking().first())
        }

        println("Time " + time)
    }

    @Test
    fun coRoutineCompose() {
        val time = measureTimeMillis {

            val single = rxSingle {
                val name = command1().observe()
                val greeting = command2(name.awaitF irst()).observe()
                val motd = command3().observe()

                "${greeting.awaitFirst()} ${motd.awaitFirst()}"
            }

            println(single.toBlocking().value())
        }

        println("Time " + time)
    }

    fun command1() = hystrixCommand("NAME") {
        Thread.sleep(1000)
        "Kotlin"
    }

    fun command2(name: String) = hystrixCommand("HELLO") {
        Thread.sleep(1000)
        "Hello $name"
    }

    fun command3() = hystrixCommand("MOTD") {
        Thread.sleep(1000)
        "have a nice day"
    }
}