package matchmaking

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import java.util.concurrent.ConcurrentHashMap

@Controller
@RequestMapping(
        path = ["/matchmaking"]
)
class MatchMakingContoller {
    val freegammes: ConcurrentHashMap<String, Int> = ConcurrentHashMap() // id's
    @RequestMapping(
            path = ["/join"],
            method = [RequestMethod.POST],
            consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun join(@RequestParam("name") name: String): ResponseEntity<String> = joinToGame(name)

    fun joinToGame(name: String): ResponseEntity<String> = when {
        freegammes.isEmpty() -> {
                val gameReq = ToServer.create()
                if (gameReq.code == 400)
                    ResponseEntity.badRequest().body("Unable to join to server")
                else {
                    freegammes[gameReq.body!!] = 1
                    ResponseEntity.ok("${gameReq.body}")
                }
            }
        else -> {
            val game = freegammes.keys().nextElement()
            freegammes.remove(game)
            freegammes[game] = freegammes[game]!! + 1
            ToServer.start(game)
            ResponseEntity.ok(game)
        }
    }
}