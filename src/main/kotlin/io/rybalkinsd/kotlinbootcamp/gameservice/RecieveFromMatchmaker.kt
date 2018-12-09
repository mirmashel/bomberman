package io.rybalkinsd.kotlinbootcamp.gameservice

import io.rybalkinsd.kotlinbootcamp.game.ConnectionHandler
import io.rybalkinsd.kotlinbootcamp.util.logger
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import java.util.concurrent.atomic.AtomicInteger

@Controller
@RequestMapping (
        path = ["/game"]
)
class RecieveFromMatchmaker {

    @Volatile var ids = AtomicInteger(0)
    val log = logger()
    @RequestMapping (
            path = ["/create"],
            method = [RequestMethod.POST]
    )
    fun create(@RequestParam("players") players: Int): ResponseEntity<String> {
        val id = ids.addAndGet(1).toString()
        ConnectionHandler.addMatch(id, players)
        log.info("Created game $id with $players players")
        return ResponseEntity.ok(id)
    }

    @RequestMapping (
            path = ["/start"],
            method = [RequestMethod.POST]
    )
    fun start(@RequestParam("gameId") gameId: String): ResponseEntity<String> {
        ConnectionHandler.startMatch(gameId)
        log.info("Game $gameId started")
        return ResponseEntity.ok(gameId)
    }


    @RequestMapping (
            path = ["/reload"],
            method = [RequestMethod.POST]
    )
    fun reload() {
        log.info("Server restarted")
        ConnectionHandler.threads.values.forEach {
            it.interrupt()
        }
        ConnectionHandler.matches.clear()
        ConnectionHandler.websocks.clear()
        ConnectionHandler.threads.clear()
        ConnectionHandler.players.clear()
    }
}