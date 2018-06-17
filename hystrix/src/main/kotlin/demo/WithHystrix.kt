package demo

import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandProperties

private object DEFAULT {
    val DEFAULT_FALLBACK = { throw RuntimeException() }
}

fun <T> hystrixCommand(group: String,
                    timeout: Int = 2000,
                    fallback: () -> T = DEFAULT.DEFAULT_FALLBACK,
                    command: () -> T): HystrixCommand<T> {
    val setter = HystrixCommand.Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey(group))
            .andCommandPropertiesDefaults(
                    HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(timeout))

    return if (fallback === DEFAULT.DEFAULT_FALLBACK) {
        object : HystrixCommand<T>(setter) {
            override fun run(): T = command()
        }
    } else {
        object : HystrixCommand<T>(setter) {
            override fun run(): T = command()

            override fun getFallback(): T = fallback()
        }
    }
}

fun <T> withHystrix(group: String,
                    timeout: Int = 1000,
                    fallback: () -> T = DEFAULT.DEFAULT_FALLBACK,
                    command: () -> T): T {

    return hystrixCommand(group, timeout, fallback, command).execute()
}