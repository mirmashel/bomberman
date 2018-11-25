package io.rybalkinsd.kotlinbootcamp.gameservice

import io.rybalkinsd.kotlinbootcamp.util.logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import java.util.concurrent.ConcurrentLinkedDeque

class GameServiceController {
    class MatchMakingContoller {
        val games: ConcurrentLinkedDeque<String> = ConcurrentLinkedDeque()
        val log = logger()
        @RequestMapping(
            path = ["game/create"],
            method = [RequestMethod.POST]
        )
        fun create(): ResponseEntity<String> =
    }
}