package demo

import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandProperties

private object DEFAULT {
    val DEFAULT_FALLBACK = { throw RuntimeException() }

    fun <T> defaultFallback(): () -> T = DEFAULT_FALLBACK
}

fun <T> withHystrix(group: String,
                    timeout: Int = 1000,
                    fallback: () -> T = DEFAULT.defaultFallback(),
                    command: () -> T): T {
    val setter = HystrixCommand.Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey(group))
            .andCommandPropertiesDefaults(
                    HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(timeout))

    val hystrixCommand = if (fallback === DEFAULT.DEFAULT_FALLBACK) {
        object : HystrixCommand<T>(setter) {
            override fun run(): T = command()
        }
    } else {
        object : HystrixCommand<T>(setter) {
            override fun run(): T = command()

            override fun getFallback(): T = fallback()
        }
    }

    return hystrixCommand.execute()
}