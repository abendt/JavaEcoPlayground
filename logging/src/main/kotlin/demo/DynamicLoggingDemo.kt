package demo

import mu.KotlinLogging
import org.slf4j.MDC

class DynamicLoggingDemo {

    private val logger = KotlinLogging.logger {}

    fun logMessage(message: String) {

        logger.error { message }
        logger.warn { message }
        logger.info { message }
        logger.debug { message }
        logger.trace { message }
    }
}

fun main(args: Array<String>) {
    DynamicLoggingDemo().logMessage("LogIt Kotlin")

    println()
    MDC.put("userId", "user1")
    DynamicLoggingDemo().logMessage("LogIt Kotlin User1")

    println()
    MDC.put("userId", "user2")
    DynamicLoggingDemo().logMessage("LogIt Kotlin User2")
}
