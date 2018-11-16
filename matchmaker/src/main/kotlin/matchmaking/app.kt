package matchmaking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Matchmaking

fun main(args: Array<String>) {
    runApplication<Matchmaking>(*args)
}