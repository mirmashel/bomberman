package matchmaking

// import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import util.logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

@Controller
@RequestMapping(
        path = ["/matchmaking"]
)
class MatchMakingContoller {
    val gamesFor2: ConcurrentLinkedDeque<String> = ConcurrentLinkedDeque()
    val gamesFor4: ConcurrentHashMap<String, Int> = ConcurrentHashMap()
    val log = logger()
    @RequestMapping(
            path = ["/join"],
            method = [RequestMethod.POST]
    )
    fun join(@RequestParam("name") name: String, @RequestParam("players") players: Int) = when(players) {
        1 -> joinToGame1(name)
        2 -> joinToGame2(name)
        4 -> joinToGame4(name)
        else -> ResponseEntity.badRequest().body("Invalid number of players")
    }

    //: ResponseEntity<String> = joinToGame1(name)

    fun joinToGame1(name: String): ResponseEntity<String> {
        val gameReg = ToServer.create(1)
        log.info("${gameReg.code}")
        return if (gameReg.code != 200) {
            ResponseEntity.badRequest().body("Unable to join to server")
        } else {
            ResponseEntity.ok("${gameReg.body}")
        }
    }

    fun joinToGame2(name: String): ResponseEntity<String> = when {
        gamesFor2.isEmpty() -> {
            val gameReq = ToServer.create(2)
            log.info("{${gameReq.code}")
            if (gameReq.code != 200)
                ResponseEntity.badRequest().body("Unable to join to server")
            else {
               // log.info("Server id: ${gameReq.body}")
                gamesFor2 += gameReq.body
                ResponseEntity.ok("${gameReq.body}")
            }
        }
        else -> {
            val game = gamesFor2.pop()
            ToServer.start(game)
            ResponseEntity.ok(game)
        }
    }

    fun joinToGame4(name: String): ResponseEntity<String> = when {
        gamesFor4.isEmpty() -> {
            val gameReq = ToServer.create(4)
            if (gameReq.code != 200)
                ResponseEntity.badRequest().body("Unable to join to server")
            else {
                // log.info("Server id: ${gameReq.body}")
                gamesFor4[gameReq.body!!] = 1
                ResponseEntity.ok("${gameReq.body}")
            }
        }
        else -> {
            val game = gamesFor4.keys().nextElement()
            gamesFor4[game] = gamesFor4[game]!! + 1
            if (gamesFor4[game] == 4) {
                ToServer.start(game)
                gamesFor4.remove(game)
            }
            ResponseEntity.ok(game)
        }
    }
}