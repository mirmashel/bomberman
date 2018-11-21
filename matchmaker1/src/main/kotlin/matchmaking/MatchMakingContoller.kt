package matchmaking

// import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import util.logger
import java.util.concurrent.ConcurrentLinkedDeque

@Controller
@RequestMapping(
        path = ["/matchmaking"]
)
class MatchMakingContoller {
    val games: ConcurrentLinkedDeque<String> = ConcurrentLinkedDeque()
    val log = logger()
    @RequestMapping(
            path = ["/join"],
            method = [RequestMethod.POST]
    )
    fun join(@RequestParam("name") name: String): ResponseEntity<String> = joinToGame1(name)


    fun joinToGame1(name: String): ResponseEntity<String> = when {
        games.isEmpty() -> {
            val gameReq = ToServer.create()
            log.info("${gameReq.code}")
            if (gameReq.code != 200)
                ResponseEntity.badRequest().body("Unable to join to server")
            else {
                games += gameReq.body
                ResponseEntity.ok("${gameReq.body}")
            }
        } else -> {
            val game = games.pop()
            ToServer.start(game)
            ResponseEntity.ok(game)
        }
    }
}