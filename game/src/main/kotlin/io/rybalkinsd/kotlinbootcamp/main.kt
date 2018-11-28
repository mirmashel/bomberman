package io.rybalkinsd.kotlinbootcamp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class ConnectHandler

fun main(args: Array<String>) {
    runApplication<ConnectHandler>(*args)
}